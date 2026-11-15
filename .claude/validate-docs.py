#!/usr/bin/env python3
"""Validate the WUtils wikis: citations, indexes, H1s, code fences, links.

There are two wikis under `docs/`, with deliberately different rules:

  docs/dev/   contributor wiki  — prose-only, cites source as `path/File.java:42`
  docs/user/  consumer wiki     — code examples encouraged, no source citations

Each wiki is also a GitBook Git Sync root: `.gitbook.yaml` points at `README.md`
(the section landing page) and `SUMMARY.md` (the rendered table of contents). Neither
is a wiki page, so both are excluded from the AGENTS.md index and checked separately.

Run from the repo root:  python3 .claude/validate-docs.py
Exits non-zero if anything is wrong.
"""
import os, re, sys, pathlib

ROOT = pathlib.Path(__file__).resolve().parent.parent
DOCS = ROOT / 'docs'

# fences: 'prose' = only ```mermaid, unless the page carries ALLOW_MARKER
#         'free'  = any code fence is fine
# cites:  'check' = every path:line must resolve and be in range
#         'forbid' = source citations do not belong on these pages
# space:  the GitBook space this wiki syncs into, so that cross-wiki links written
#         as https://app.gitbook.com/s/<space>/<page path> still resolve to a real file.
WIKIS = {
    'docs/dev':  {'fences': 'prose', 'cites': 'check',  'space': 'BlHpY9Cbyf7QuSQXTKKu'},
    'docs/user': {'fences': 'free',  'cites': 'forbid', 'space': 'ZQ50kJ36ioYPFvndW1LL'},
}
ALLOW_MARKER = '<!-- allow-code-fences -->'
# Not wiki pages: the AGENTS.md index, and the two files GitBook Git Sync consumes.
NON_PAGES = {'AGENTS.md', 'README.md', 'SUMMARY.md'}

problems = []
def rel(p): return str(p.relative_to(ROOT))

def pages_of(root):
    return sorted((p for p in root.rglob('*.md') if p.name not in NON_PAGES),
                  key=lambda p: sort_key(rel(p)))

def sort_key(path):
    """Module overview (dir/dir.md) sorts before its sibling topic pages."""
    p = pathlib.Path(path)
    return (str(p.parent), 0 if p.stem == p.parent.name else 1, p.name)

# --- source files, for citation resolution ----------------------------------
by_name = {}
for p in ROOT.rglob('*'):
    if not p.is_file() or '/build/' in str(p) or '/.git/' in str(p):
        continue
    if p.suffix in ('.java', '.kt', '.gradle') or p.name.endswith('.gradle.kts'):
        by_name.setdefault(p.name, []).append(p)

CITE = re.compile(r'`?((?:[\w./-]+/)?([\w.-]+\.(?:java|kt|gradle|kts))):(\d+)(?:-(\d+))?`?')

def check_citations(md, text, mode):
    for m in CITE.finditer(text):
        full, name, a, b = m.group(1), m.group(2), int(m.group(3)), int(m.group(4) or m.group(3))
        if mode == 'forbid':
            problems.append(f"{rel(md)}: source citation '{full}:{a}' — the user wiki does not "
                            f"cite source lines; name the class, or link the contributor wiki")
            continue
        cand = ROOT / full
        if cand.is_file():
            target = cand
        else:
            hits = by_name.get(name, [])
            if len(hits) != 1:
                problems.append(f"{rel(md)}: '{full}:{a}' is ambiguous or missing "
                                f"({len(hits)} files named {name}) — cite a full repo-relative path")
                continue
            target = hits[0]
        n = len(target.read_text().splitlines())
        if a < 1 or b > n:
            problems.append(f"{rel(md)}: {full}:{a}-{b} out of range ({rel(target)} has {n} lines)")

def check_fences(md, text, mode):
    if mode == 'free' or ALLOW_MARKER in text:
        return
    open_at = None   # line number of the ```mermaid we are inside, else None
    for i, line in enumerate(text.splitlines(), 1):
        if not line.startswith('```'):
            continue
        info = line.strip()[3:].strip()
        if open_at is not None:
            # Inside a mermaid block: a bare ``` closes it, anything else is nested junk.
            if info:
                problems.append(f"{rel(md)}:{i}: '```{info}' inside the mermaid block "
                                f"opened at line {open_at}")
            else:
                open_at = None
        elif info == 'mermaid':
            open_at = i
        elif info:
            problems.append(f"{rel(md)}:{i}: code fence '```{info}' — contributor wiki is "
                            f"prose-only (only ```mermaid diagrams allowed)")
        else:
            problems.append(f"{rel(md)}:{i}: unmatched closing fence — contributor wiki is "
                            f"prose-only (only ```mermaid diagrams allowed)")
    if open_at is not None:
        problems.append(f"{rel(md)}:{open_at}: ```mermaid block is never closed")

# --- links between pages must resolve ---------------------------------------
LINK = re.compile(r'\[[^\]]*\]\(([^)#\s]*)(?:#([^)\s]*))?\)')
CROSS_SPACE = re.compile(r'https://app\.gitbook\.com/(?:o/[^/]+/)?s/([^/]+)/?(.*)$')

def gitbook_slug(s):
    """A page path segment or heading anchor, slugged the way GitBook does it.

    Checked against every heading on the published site: `<`/`>` are spelled out,
    parentheses and slashes separate words, other punctuation is dropped (dots and
    underscores survive), and runs of hyphens collapse — so `close()` vs. `_finalize()`
    is #close-vs.-_finalize, not the GitHub-style #close-vs-_finalize.
    """
    s = s.lower().replace('<', ' less than ').replace('>', ' greater than ')
    s = re.sub(r'[()]', ' ', s)
    s = re.sub(r'[^\w\s./-]', '', s)
    s = re.sub(r'[\s/]+', '-', s)
    return re.sub(r'-+', '-', s).strip('-')

def page_paths(wiki):
    """Map each GitBook page path in a wiki to its file, as Git Sync derives it.

    GitBook paths come from SUMMARY.md, not from the directory tree: a page's path is
    its `## group` heading slug plus its file's stem, so docs/user/i18n/i18n.md is
    published at text/i18n and docs/dev/jdbc/jdbc.md at storage/jdbc.
    """
    summary = ROOT / wiki / 'SUMMARY.md'
    group, paths = None, {'': ROOT / wiki / 'README.md'}
    if not summary.is_file():
        return paths
    for line in summary.read_text().splitlines():
        if line.startswith('## '):
            group = gitbook_slug(line[3:])
        m = re.match(r'\s*\* \[[^\]]*\]\(([^)]+)\)', line)
        if m and m.group(1) != 'README.md':
            stem = gitbook_slug(pathlib.Path(m.group(1)).stem)
            paths[f'{group}/{stem}' if group else stem] = ROOT / wiki / m.group(1)
    return paths

SPACES = {rules['space']: name for name, rules in WIKIS.items()}
PAGE_PATHS = {name: page_paths(name) for name in WIKIS}

def cross_space_target(md, t):
    """Resolve https://app.gitbook.com/s/<space>/<path> to the file it names.

    The two wikis sync into separate GitBook spaces, and links never cross a space
    boundary by relative path — GitBook rewrites one that escapes its sync root, or
    a bare site path like /contributing/common/events, into a GitHub tree URL that
    does not exist. Cross-wiki links therefore use the space's app URL, which GitBook
    resolves to the published page; they still have to name a real page.
    """
    m = CROSS_SPACE.match(t)
    if not m or m.group(1) not in SPACES:
        return None
    wiki, path = SPACES[m.group(1)], m.group(2).rstrip('/')
    page = PAGE_PATHS[wiki].get(path)
    if page is None:
        problems.append(f"{rel(md)}: cross-space link {t} names no page in {wiki}/SUMMARY.md "
                        f"(GitBook paths are '<group heading>/<file stem>')")
    return page

anchor_cache = {}
def anchors_of(f):
    if f not in anchor_cache:
        text = re.sub(r'^```.*?^```', '', f.read_text(), flags=re.S | re.M)
        anchor_cache[f] = {gitbook_slug(h) for h in re.findall(r'^#{1,6}\s+(.*)$', text, re.M)}
    return anchor_cache[f]

def check_anchor(md, t, frag, page):
    if frag and page.suffix == '.md' and frag not in anchors_of(page):
        problems.append(f"{rel(md)}: anchor #{frag} in link {t or '(same page)'} matches no "
                        f"heading in {rel(page)} (GitBook slugs: `close()` vs. x -> close-vs.-x)")

def check_links(md, text):
    for target in LINK.finditer(text):
        t, frag = target.group(1), target.group(2)
        if not t:
            check_anchor(md, t, frag, md)
            continue
        if t.startswith(('http://', 'https://')):
            page = cross_space_target(md, t)
            if page is not None:
                check_anchor(md, t, frag, page)
            continue
        if t.startswith('mailto:'):
            continue
        if t.startswith('/'):
            problems.append(f"{rel(md)}: site path {t} — GitBook turns it into a broken GitHub "
                            f"link; link the other wiki as https://app.gitbook.com/s/<space>/<path>")
            continue
        # Every link resolves from the file it appears in — including the AGENTS.md
        # indexes, which live inside their wiki root rather than at the repo root.
        # Repo-relative targets look right in a diff and 404 in every viewer.
        page = (md.parent / t).resolve()
        if not page.is_file():
            hint = ""
            if t.startswith('docs/') and (ROOT / t).is_file():
                hint = f" — repo-relative; from {rel(md.parent)}/ write {os.path.relpath(ROOT / t, md.parent)}"
            problems.append(f"{rel(md)}: dead link {t}{hint}")
            continue
        check_anchor(md, t, frag, page)

def check_description(md, text):
    """Every page needs frontmatter `description:`.

    GitBook builds /llms.txt from the published site, one line per page:
    `- [Title](url.md): description`. A page without a description still gets
    listed, just stripped down to its title — so the index tells an AI tool the
    page exists and nothing about when to read it.
    """
    if not text.startswith('---\n'):
        problems.append(f"{rel(md)}: no frontmatter — every page needs a "
                        f"`description:` for the site's /llms.txt entry")
        return
    end = text.find('\n---', 4)
    front = text[4:end] if end != -1 else ''
    body = [l for l in front.splitlines()]
    if not any(l.startswith('description:') for l in body):
        problems.append(f"{rel(md)}: frontmatter has no `description:` — needed for "
                        f"the site's /llms.txt entry")
        return
    i = next(i for i, l in enumerate(body) if l.startswith('description:'))
    value = body[i][len('description:'):].strip()
    if value in ('>-', '>', '|', '|-', ''):
        value = ' '.join(l.strip() for l in body[i + 1:]
                         if l.startswith((' ', '\t'))).strip()
    if not value:
        problems.append(f"{rel(md)}: `description:` is empty")
    elif len(value) > 256:
        problems.append(f"{rel(md)}: description is {len(value)} chars "
                        f"(GitBook allows 256)")

def first_h1(f):
    """The page's H1, skipping any GitBook YAML frontmatter block."""
    lines = f.read_text().splitlines()
    if lines and lines[0].strip() == '---':
        end = next((i for i, l in enumerate(lines[1:], 1) if l.strip() == '---'), None)
        lines = lines[end + 1:] if end is not None else lines
    return next((l.lstrip('#').strip() for l in lines if l.startswith('# ')), '')

# --- per-wiki checks --------------------------------------------------------
counts = {}
for name, rules in WIKIS.items():
    root = ROOT / name
    if not root.is_dir():
        problems.append(f"missing wiki root {name}/"); continue
    index = root / 'AGENTS.md'
    if not index.is_file():
        problems.append(f"missing index {name}/AGENTS.md"); continue

    # GitBook Git Sync reads these three; without them the section will not build.
    readme, summary = root / 'README.md', root / 'SUMMARY.md'
    for f in (root / '.gitbook.yaml', readme, summary):
        if not f.is_file():
            problems.append(f"missing {rel(f)} — required for GitBook Git Sync")

    on_disk = [str(p.relative_to(root)) for p in pages_of(root)]
    counts[name] = len(on_disk)
    extra = [f for f in (readme,) if f.is_file()]
    for md in pages_of(root) + [index] + extra:
        text = md.read_text()
        check_citations(md, text, rules['cites'])
        check_fences(md, text, rules['fences'])
        check_links(md, text)
        if md is not index:
            check_description(md, text)
    if summary.is_file():
        check_links(summary, summary.read_text())

    links = re.findall(r'^- \[([^\]]+)\]\(([^)]+)\)', index.read_text(), re.M)
    listed = [l[1] for l in links]
    if listed != on_disk:
        problems.append(f"{name}/AGENTS.md order/content mismatch\n"
                        f"  listed  : {listed}\n  expected: {on_disk}")
    for text, path in links:
        f = root / path
        if not f.is_file():
            continue  # already reported by check_links
        h1 = first_h1(f)
        if h1 != text:
            problems.append(f"{name}/AGENTS.md: link text {text!r} != H1 {h1!r} in {path}")

    # SUMMARY.md is the sidebar GitBook renders. Its order is reader-facing and
    # deliberately unlike disk order, so compare membership, not sequence — but every
    # page must appear exactly once, or it is unreachable on the site.
    if not summary.is_file():
        continue
    entries = re.findall(r'^\s*\* \[([^\]]+)\]\(([^)]+)\)', summary.read_text(), re.M)
    listed = [path for _, path in entries if path != 'README.md']
    if [path for _, path in entries][:1] != ['README.md']:
        problems.append(f"{name}/SUMMARY.md: first entry must link README.md")
    for path in sorted(set(listed)):
        if listed.count(path) > 1:
            problems.append(f"{name}/SUMMARY.md: {path} listed {listed.count(path)} times")
    missing = [p for p in on_disk if p not in listed]
    unknown = [p for p in listed if p not in on_disk]
    if missing:
        problems.append(f"{name}/SUMMARY.md: not in the sidebar, so unreachable on the "
                        f"site: {', '.join(missing)}")
    if unknown:
        problems.append(f"{name}/SUMMARY.md: listed but not a wiki page: "
                        f"{', '.join(unknown)}")
    for text, path in entries:
        f = root / path
        if not f.is_file():
            continue  # already reported by check_links
        h1 = first_h1(f)
        if h1 != text:
            problems.append(f"{name}/SUMMARY.md: link text {text!r} != H1 {h1!r} in {path}")

# --- the root pointer page --------------------------------------------------
root_index = DOCS / 'AGENTS.md'
if root_index.is_file():
    check_links(root_index, root_index.read_text())

# --- stray pages outside a known wiki root ----------------------------------
known = {ROOT / n for n in WIKIS}
for p in DOCS.rglob('*.md'):
    if not any(k in p.parents for k in known) and p != DOCS / 'AGENTS.md':
        problems.append(f"{rel(p)}: not inside a known wiki root ({', '.join(WIKIS)})")

if problems:
    print(f"{len(problems)} problem(s):\n")
    for p in problems: print(" -", p)
    sys.exit(1)

exempt = sorted(rel(p) for p in (ROOT / 'docs/dev').rglob('*.md') if ALLOW_MARKER in p.read_text())
note = f" ({', '.join(exempt)} exempt from the prose-only rule)" if exempt else ""
summary = ", ".join(f"{n} {c} pages" for n, c in counts.items())
print(f"OK — {summary}; indexes consistent, links and citations resolve{note}.")
