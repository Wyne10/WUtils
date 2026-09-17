---
description: >-
  Module by module, package by package: internal structure, invariants, sharp
  edges and source citations for people changing WUtils itself.
---

# Contributing to WUtils

WUtils is a multi-module collection of independently versioned and independently published Java/Kotlin libraries for Bukkit/Paper 1.16.5 plugins. Each module is its own Maven Central artifact, and consumers pull only the modules they need.

This section documents WUtils at the level of detail someone **changing the code** needs: internal structure, invariants, sharp edges, and `path/to/File.java:<line>` citations back into the repository. It covers the `animation`, `common`, `commonKt`, `config`, `configurables`, `i18n`, `i18nKt`, `jdbc`, `json`, and `structure` modules. The deprecated `log` module is excluded from the build and is not documented.

{% hint style="warning" %}
These pages describe internals and are deliberately prose-only — they are not an API contract. If you want to _use_ WUtils in a plugin rather than work on it, read the [Documentation](../../documentation/) section instead.
{% endhint %}

## Module map

| Module                                          | Artifact               | What lives there                                                                      |
| ----------------------------------------------- | ---------------------- | ------------------------------------------------------------------------------------- |
| [common](common/common.md)                      | `wutils-common`        | Events, scheduler, promises, terminables, game-object helpers — the shared foundation |
| [commonKt](commonKt/commonKt.md)                | `wutils-common-kotlin` | Kotlin extensions over `common`                                                       |
| [config](config/config.md)                      | `wutils-config`        | Annotation-driven YAML config generation and reading                                  |
| [configurables](configurables/configurables.md) | `wutils-configurables` | Config-driven items, GUIs, animations, interactions                                   |
| [i18n](i18n/i18n.md)                            | `wutils-i18n`          | Languages, interpreters, replacements, localized values                               |
| [i18nKt](i18nKt/i18nKt.md)                      | `wutils-i18n-kotlin`   | Kotlin extensions over `i18n`                                                         |
| [animation](animation/animation.md)             | `wutils-animation`     | Step scheduling and runnables                                                         |
| [structure](structure/structure.md)             | `wutils-structure`     | Schemes, regions, modifiers, persistence                                              |
| [jdbc](jdbc/jdbc.md)                            | `wutils-jdbc`          | Pooled connections and runtime driver loading                                         |
| [json](json/json.md)                            | `wutils-json`          | Annotation-driven JSON field storage                                                  |

## Documentation conventions

* Contributor pages are **prose-only**. Only ` ```mermaid ` diagrams are allowed; pages that genuinely need code opt out with an `allow-code-fences` HTML comment.
* Source is cited as `path/to/File.java:<line>`, repo-relative. Citations are checked against the working tree, including line ranges.
* Both wikis are validated by `python3 .claude/validate-docs.py`, which verifies the `AGENTS.md` indexes and `SUMMARY.md` tables of contents match what is on disk, and that every link and citation resolves.
