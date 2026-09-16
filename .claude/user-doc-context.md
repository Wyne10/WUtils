# WUtils user wiki — writing contract

Briefing for agents writing `docs/user/`. Read `.claude/doc-context.md` first for the
project facts (module map, artifacts, versions, dependency graph, vendored code). This
file **overrides the `wiki-writer` agent definition** wherever the two disagree.

## There are two wikis now

| Wiki | Root | Reader | Style |
|---|---|---|---|
| Contributor | `docs/dev/` | someone changing WUtils itself | internals, invariants, sharp edges, `File.java:42` citations, prose-only |
| User | `docs/user/` | someone adding WUtils to their plugin | task-oriented, code examples, no citations |

`docs/dev/` is finished, accurate and verified. **Do not edit it.** You are writing
`docs/user/` from scratch.

## Who you are writing for

A competent Bukkit/Paper plugin developer who has just added `wutils-something` to their
build file and wants to get something working. They do not have the WUtils source open.
They will never read it. They want to know: what can this do for me, what do I type, and
what will bite me.

Write in second person. "You register the listener", not "the consumer registers the
listener" and not "one registers the listener".

## The rules that changed from the contributor wiki

**Code examples are now required, not banned.** Every page must show real usage. A page
that describes an API in prose without ever showing a call is a failed page. Fence them
as ```java, ```kotlin, ```yaml, ```groovy or ```kotlin for build files.

**No source citations.** Never write `common/src/main/java/.../Events.java:42`. Never
write `Events.java:42`. The validator rejects both. Name the class — `Events`,
`ItemConfigurable` — and let the reader's IDE do the rest. If some behaviour genuinely
needs the source to explain, link the contributor page instead:
`[how this resolves](../../dev/common/events.md)`.

**No internals for their own sake.** The reader does not care that `AttributeMap` is a
`LinkedHashMap` keyed by config key. They care that YAML key order does not decide
application order. Document the *consequence*, skip the mechanism — unless the mechanism
is the only way to make the consequence make sense.

**Sharp edges stay.** The contributor wiki's "sharp edges" sections are the single most
valuable thing in it for a user. Keep every one that can bite a consumer, rewritten as
advice: what goes wrong, what it looks like when it does, what to write instead. Drop the
ones that only matter to someone editing WUtils.

## Examples must be real

You are writing code someone will paste. Getting it wrong is worse than omitting it.

- Read the actual method signatures before you write a call. Do not guess a method name,
  a parameter order, or a builder chain from what the dev wiki prose implies.
- Prefer short, complete, plausible snippets over fragments with `...` in the middle.
  Include the imports only when the class is ambiguous (e.g. two `Ticks`).
- YAML examples must match what the parser actually accepts — these modules are
  config-driven and a wrong key is the most likely way a reader gets stuck. The
  contributor wiki documents the exact grammars; use them.
- If you cannot verify an example against the source, do not write it. Say the API exists
  and move on. Flag it in your report.
- Java is the default. Show Kotlin only on the Kotlin page, or where the Kotlin API is
  genuinely different from the Java one.

## Dependencies — get this right on every module page

Every module page opens with how to depend on it. Maven Central, group `io.github.wyne10`.
Show the Gradle Kotlin DSL form, and the version from `.claude/doc-context.md`:

```kotlin
dependencies {
    implementation("io.github.wyne10:wutils-common:1.16.4")
}
```

Then state, plainly, which third-party dependencies the reader must supply themselves.
Most WUtils third-party integrations are `compileOnly` — WUtils compiles against them and
does not ship them, so a missing one is a `NoClassDefFoundError` at runtime, not a build
failure. This is the most likely way a reader breaks their plugin. Be explicit about
which features need which optional dependency.

Also mention shading/relocation where it matters: these are libraries, so the reader is
usually shading them into their plugin jar.

## Page structure

Open with a short paragraph on what the module is for and when you would reach for it —
including when you would *not*. Then dependency coordinates. Then the common tasks,
biggest first, each with an example. Then the sharp edges. Then a "See also" list.

Use `##` headings that name a task ("Sending a message to a player") rather than a type
("`LocalizedString`"). Keep paragraphs to a few sentences. Tables are good for
enumerating options, keys, and flags.

Diagrams are welcome as ```mermaid fences where a flow is genuinely hard to say in words.

Target roughly 120–250 lines per page. A page much shorter than that is probably missing
its examples; much longer and it should have been split.

## Cross-linking

- Link sibling user pages with relative links: `[Configuration](../config/config.md)`.
- Link the contributor wiki for anything a curious reader might want to go deeper on:
  `[the full grammar](../../dev/structure/modifiers.md)`. Do this at most a few times per
  page — it is an escape hatch, not the main path.
- Link out to upstream docs for third-party APIs (Paper, Adventure, WorldEdit, lucko's
  helper at https://github.com/lucko/helper/wiki). Never re-document them.

## What you must not touch

- `docs/dev/**` — the contributor wiki is finished.
- `docs/user/AGENTS.md` — the conductor session owns the index. It is already written and
  already lists your pages. Create your files at exactly the paths you were assigned, with
  exactly the H1 you were given, or the index will not match.
- `.claude/**` — including this file and the validator.
- Any source file.

Other agents are writing sibling pages at the same time. Only ever write your own files.

## Before you report

Run `python3 .claude/validate-docs.py`. Because siblings are still being written, it may
report missing pages and dead links you do not own — ignore those. Fix every problem it
reports **in a file you own**, especially:

- source citations (the user wiki forbids them)
- dead links
- an H1 that does not match the index

Then report: pages written, examples you could not verify, and anything you deliberately
left out.
