# WUtils Documentation

Two wikis, for two different readers.

- **[User wiki](user/AGENTS.md)** (`docs/user/`) — you are adding a WUtils module to your
  plugin. How to depend on it, what it does, worked examples, and what will bite you.
  Code examples throughout; no source references.
- **[Contributor wiki](dev/AGENTS.md)** (`docs/dev/`) — you are changing WUtils itself.
  Module by module, package by package: internal structure, invariants, sharp edges, and
  `path/to/File.java:<line>` citations. Prose-only by convention.

Each wiki's `AGENTS.md` indexes every page under it. Both are checked by
`python3 .claude/validate-docs.py`, which verifies the indexes match what is on disk,
that links and citations resolve, and that each wiki keeps to its own code-fence rule.
The writing contracts live in `.claude/doc-context.md` (project facts, shared) and
`.claude/user-doc-context.md` (user wiki style).

## Publishing

Both wikis are published to the **Wyne Docs** GitBook site, each as its own section,
by **per-space Git Sync** — one sync per space, direction **Git → GitBook**:

| Wiki | Site section | Git Sync project directory |
|---|---|---|
| `docs/user/` | `/documentation` | `docs/user` |
| `docs/dev/` | `/contributing` | `docs/dev` |

Wyne Docs hosts more than one project, and each project keeps its documentation in its
own repository next to the code it describes — this wiki cites source lines that are
checked against the working tree, so it cannot move to a shared docs repository. That
rules out site-level Git Sync, which maps a single repository onto a whole site: there is
no `gitbook-docs.yaml` here, and the site's structure (sections, section groups, and the
`WUtils` group these two sections sit in) is managed in the GitBook UI rather than in
this repository. Site structure is shared across projects, so no one project repository
should own it.

Each wiki root holds the three files GitBook reads for its space, all of them checked by
the validator:

- `.gitbook.yaml` — points GitBook at the README and SUMMARY below.
- `README.md` — the section landing page. Not a wiki page, so it is not in `AGENTS.md`.
- `SUMMARY.md` — the sidebar GitBook renders. Its order is reader-facing and deliberately
  unlike disk order, but every page must appear exactly once or it is unreachable on the
  site.

Section paths cannot contain a slash — GitBook rewrites `wutils/documentation` to
`wutils-documentation` — so per-project URL namespacing would have to be a flat prefix.
The generic paths are kept for now; a section's path is cheap to change later.

Every page carries a frontmatter `description:`, checked by the validator. GitBook
serves an auto-generated `/llms.txt` on the published site — one line per page, each a
Markdown link to the page followed by a colon and its description — so a page without
one is listed by title alone, telling an AI tool that the page exists and nothing about
when to read it. There is no
`llms.txt` in this repository and there should not be: GitBook builds it, along with
`/llms-full.txt` and a `.md` version of every page.

Because the two wikis sync as separate sections, a relative link from one into the other
would escape its sync root and 404 on the site. Cross-wiki links are therefore written as
site paths — `/contributing/common/events`, not `../../dev/common/events.md`. The
validator resolves those back to files, so they stay checked; the tradeoff is that they
point at the site rather than the file when the repository is browsed on GitHub.

Git Sync itself is configured in the GitBook UI, per space, and is not part of this
repository.
