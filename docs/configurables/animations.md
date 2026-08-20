# Animations

`AnimationConfigurable` describes a whole timed sequence — particles, sounds, fireworks,
titles, knockback, messages — as YAML, and builds it into an
[`Animation`](../animation/animation.md) at runtime.

- `configurables/src/main/java/me/wyne/wutils/config/configurables/AnimationConfigurable.java`
- `.../configurables/AnimationStepConfigurable.java`
- attributes in `.../configurables/animation/attribute/`

**Requires the optional `:WUtils-animation` dependency** at runtime, on top of i18n. See
the [module overview](configurables.md#dependencies).

## The shape

An animation is a section of **steps**. Each step is an `AttributeConfigurable` whose
attributes fall into three roles:

- **Type** — `type` picks `BLOCKING` (the animation waits for this step to finish) or
  `PARALLEL` (it starts and the animation moves on).
- **Timings** — `delay`, `period`, `duration`, each implementing
  `TimingsAnimationAttribute` (`.../animation/TimingsAnimationAttribute.java:9-14`).
- **Effects** — everything else, each implementing `ContextAnimationAttribute`
  (`.../animation/ContextAnimationAttribute.java:16-18`), a factory from an
  `AnimationContext` to an `AnimationRunnable`.

`AnimationStepConfigurable.build` (`AnimationStepConfigurable.java:57-68`) applies the
timing attributes onto a mutable `AnimationTimings`, collects the effect attributes into
runnables, wraps several into a `CompositeRunnable`, and hands the result to the chosen
step type. **A step can therefore carry any number of simultaneous effects**, which is
the main reason to prefer one step with three effects over three steps.

<!-- allow-code-fences -->

## A working example

```yaml
ritual:

  # Step names are arbitrary labels, but order matters — steps run in the
  # order the section lists them.
  charge:
    type: PARALLEL         # BLOCKING (default) waits; PARALLEL runs alongside
    delay: 0               # ticks before first run. 't', 's', 'ms' all parse.
    period: 2t             # ticks between repeats; 0 means run once
    duration: 3s           # total run time

    # Several effects in one step run together on every tick of that step.
    worldParticle: 'FLAME 20 0.05 0,1,0'    # particle count extra offset [data]
    localSound: 'BLOCK_BEACON_ACTIVATE 1.0 1.2'

  pull:
    repeat: 3              # duplicate this whole step 3x, back to back
    period: 1t
    duration: 1s
    forceField:            # radius, velocity, offset from the animation location
      radius: 3.0
      velocity: 0.4
      offset: 0,1,0

  payoff:
    delay: 1s
    firework:
      power: 1
      offset: 0,1,0
      effects:
        burst:             # child names are arbitrary
          type: BALL_LARGE
          flicker: true
          colors:
            first: '#FF0000'
            second:
              red: 0
              green: 255
              blue: 0
          fadeColors:
            first: '#FFFFFF'

    # A step can embed a full interaction — see interactions.md
    interaction:
      toAll: true
      message: '<gold>The ritual completes.'
```

## The attribute vocabulary

Registered in `AnimationStepConfigurable.java:23-39`; keys in
`.../configurables/animation/AnimationAttribute.java:10-24`.

| Key | Role | Value |
|---|---|---|
| `type` | type | `BLOCKING` (default) or `PARALLEL` (`animation/attribute/AnimationTypeAttribute.java:52-65`) |
| `delay` | timing | duration expression → ticks |
| `period` | timing | duration expression → ticks |
| `duration` | timing | duration expression → ticks |
| `anchorCharge` | effect | int charge level; needs a location |
| `forceField` | effect | `radius velocity offset`, or a section; needs a location |
| `playerTitle` | effect | title/subtitle/times; needs a player |
| `localSound` | effect | sound at the location; needs a location |
| `worldParticle` | effect | particle at the location; needs a location |
| `firework` | effect | a section; needs a location |
| `playerMessage` | effect | string list to the context player |
| `globalMessage` | effect | string list to every online player |
| `interaction` | effect | a nested [interaction](interactions.md); needs a player |
| `interactions` | effect | a nested interaction list; needs a player |

All three timing keys parse through `ConfigUtils.getTicks`, so they accept the
[duration expressions](../common/durations.md) `20`, `20t`, `1s`, `1500ms` and
combinations like `1m30s`.

### `repeat`

`AnimationConfigurable.fromConfig` (`AnimationConfigurable.java:69-82`) reads a
`repeat` int from each step section and appends that many identical steps. It is handled
by the enclosing animation, not by the attribute map, so it never appears as an
attribute. Verified: a three-step section where the middle step has `repeat: 3` produces
five steps.

Each copy is a distinct `AnimationStepConfigurable` reading the same section, so all
copies share the same timings — `repeat` is "do this whole step N times in sequence",
different from `period`/`duration`, which is "tick this step repeatedly for a while".

### AnimationContext

`AnimationContext` (`.../configurables/animation/AnimationContext.java:20-82`) carries a
nullable `Player`, a nullable `Location`, and the text/component replacements. Every
effect attribute checks what it needs and **returns `AnimationRunnable.EMPTY` if it is
missing** — a location-based effect with no location does nothing, silently. That makes
partially-supplied contexts safe, and makes a mis-set-up animation look like a config
problem when it is a code problem.

`AnimationConfigurable.build(Plugin, AnimationContext)` (`AnimationConfigurable.java:84-90`)
constructs the `Animation`; see [Steps and scheduling](../animation/steps.md) for what
happens next.

## Sharp edges

- **An empty step is legal and does nothing.** With no effect attributes, `build`
  produces a `CompositeRunnable` over an empty list rather than failing
  (`AnimationStepConfigurable.java:62-66`).
- **Step order comes from the YAML here, not from a registry.**
  `AnimationConfigurable.fromConfig` iterates `config.getKeys(false)`, so unlike
  attributes within a step, steps run in file order.

## See also

- [WUtils Animation](../animation/animation.md) — the runtime this builds: what
  `BLOCKING` vs `PARALLEL` actually schedule, and the runnables these attributes create.
- [Attributes and Containers](attributes.md) — how step keys are resolved.
- [Interactions](interactions.md) — the `interaction` and `interactions` effects.
- [Particle Data Parsers](../common/particles.md) — the `data` grammar.
- [Durations and Cooldowns](../common/durations.md) — the timing expressions.
