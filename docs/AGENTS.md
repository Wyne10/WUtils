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

Both wikis are published to the **Wyne Docs** GitBook site via Git Sync, each as its own
section, syncing **Git → GitBook** from this repository:

| Wiki | Site section | Git Sync root directory |
|---|---|---|
| `docs/user/` | `/documentation` | `docs/user` |
| `docs/dev/` | `/contributing` | `docs/dev` |

Each root holds the three files GitBook reads, all of them checked by the validator:

- `.gitbook.yaml` — points GitBook at the README and SUMMARY below.
- `README.md` — the section landing page. Not a wiki page, so it is not in `AGENTS.md`.
- `SUMMARY.md` — the sidebar GitBook renders. Its order is reader-facing and deliberately
  unlike disk order, but every page must appear exactly once or it is unreachable on the
  site.

Because the two wikis sync as separate sections, a relative link from one into the other
would escape its sync root and 404 on the site. Cross-wiki links are therefore written as
site paths — `/contributing/common/events`, not `../../dev/common/events.md`. The
validator resolves those back to files, so they stay checked; the tradeoff is that they
point at the site rather than the file when the repository is browsed on GitHub.

Git Sync itself is configured in the GitBook UI, per space, and is not part of this
repository.
