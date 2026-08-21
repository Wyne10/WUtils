# WUtils Documentation

Two wikis, for two different readers.

- **[User wiki](user/AGENTS.md)** (`docs/user/`) — you are adding a WUtils module to your
  plugin. How to depend on it, what it does, worked examples, and what will bite you.
  Code examples throughout; no source references.
- **[Contributor wiki](dev/AGENTS.md)** (`docs/dev/`) — you are changing WUtils itself.
  Module by module, package by package: internal structure, invariants, sharp edges, and
  `path/to/File.java:42` citations. Prose-only by convention.

Each wiki's `AGENTS.md` indexes every page under it. Both are checked by
`python3 .claude/validate-docs.py`, which verifies the indexes match what is on disk,
that links and citations resolve, and that each wiki keeps to its own code-fence rule.
The writing contracts live in `.claude/doc-context.md` (project facts, shared) and
`.claude/user-doc-context.md` (user wiki style).
