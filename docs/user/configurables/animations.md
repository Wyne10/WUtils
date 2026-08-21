# Animation Configurables

`AnimationConfigurable` describes a whole timed sequence — particles, sounds,
fireworks, titles, force fields, messages — as YAML, and turns it into an
[`Animation`](../animation/animation.md) you run at runtime. Reach for it when you
want a server owner to redesign an effect (a boss's death sequence, a ritual, a
level-up flourish) without you touching code; for a one-off effect you'll never
expose to config, building an `Animation` directly in code is simpler.

An animation is a section of **steps**, run in the order they appear in the file.
Each step can carry any number of simultaneous effects — prefer one step with three
effects over three separate steps when they're meant to happen together.

## What you need

`AnimationConfigurable` lives in `wutils-configurables` (see
[Configurables](configurables.md) for coordinates), and needs two things on your
runtime classpath on top of that, both `compileOnly` on the module's side:

- **`wutils-animation`** — the runtime this builds into. See
  [WUtils Animation](../animation/animation.md). Without it: `NoClassDefFoundError`
  the moment you touch an `AnimationConfigurable`.
- **`wutils-i18n`** — needed for the `interaction`/`interactions` effects and any
  title text. Without it: `NoClassDefFoundError` the first time a step with one of
  those effects runs.

## A complete example

```yaml
ritual:

  # Step names are arbitrary labels, but order matters - steps run in the
  # order this section lists them.
  charge:
    type: PARALLEL         # BLOCKING (default) waits for the step to finish;
                            # PARALLEL starts it and moves on immediately
    delay: 0                # ticks before the first run - 't'/'s'/'ms' all parse
    period: 2t               # ticks between repeats; 0 means run once
    duration: 3s              # total run time

    # Several effects in one step run together on every tick of that step.
    worldParticle: 'FLAME 20 0.05 0,1,0'    # particle count extra offset [data]
    localSound: 'BLOCK_BEACON_ACTIVATE 1.0 1.2'

  pull:
    repeat: 3               # duplicate this whole step 3x, back to back
    period: 1t
    duration: 1s
    forceField:              # radius, velocity, offset from the animation location
      radius: 3.0
      velocity: 0.4
      offset: 0,1,0

  payoff:
    delay: 1s
    firework:
      power: 1
      offset: 0,1,0
      effects:
        burst:                # child names are arbitrary
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

    # A step can embed a full interaction - see interactions.md
    interaction:
      toAll: true
      message: '<gold>The ritual completes.'
```

## Declaring and playing it

```java
public class Effects {
    @ConfigEntry(section = "effects")
    public AnimationConfigurable ritual = new AnimationConfigurable();
}
```

```java
import me.wyne.wutils.config.configurables.animation.AnimationContext;

var context = new AnimationContext(player, player.getLocation());
Animation animation = effects.ritual.build(plugin, context);
animation.run();
```

`AnimationContext` carries an optional player, an optional location, and any text or
component replacements. Every effect checks what it needs and quietly does nothing
if the context doesn't supply it — a `worldParticle` effect with no location produces
no particles, but the animation itself still runs to completion. That makes a
partially-supplied context safe to pass around, but it also means a step that "does
nothing" is worth checking your context for before you go looking for a config bug.

See [WUtils Animation](../animation/animation.md) for what `run()` actually schedules
and how `BLOCKING` vs `PARALLEL` steps chain together.

## Key reference

| Key | Role | Value |
|---|---|---|
| `type` | step type | `BLOCKING` (default) or `PARALLEL` |
| `delay` | timing | duration expression → ticks |
| `period` | timing | duration expression → ticks |
| `duration` | timing | duration expression → ticks |
| `repeat` | step count | int; duplicates the whole step this many times (see below) |
| `anchorCharge` | effect | int charge level; needs a location |
| `forceField` | effect | `radius velocity offset`, or a section; needs a location |
| `playerTitle` | effect | title/subtitle/times section; needs a player |
| `localSound` | effect | sound at the location; needs a location |
| `worldParticle` | effect | particle at the location; needs a location |
| `firework` | effect | a section; needs a location |
| `playerMessage` | effect | string list, to the context player |
| `globalMessage` | effect | string list, to every online player |
| `interaction` | effect | a nested [interaction](interactions.md); needs a player |
| `interactions` | effect | a nested interaction list; needs a player |

All three timing keys and `fadeIn`/`stay`/`fadeOut` inside `playerTitle` accept the
same [duration expressions](../common/values.md) as everywhere else in WUtils: `20`,
`20t`, `1s`, `1500ms`, `1m30s`.

`worldParticle`'s string form is `"<particle> <count> <extra> <offset> [data]"` —
`offset` is `x,y,z`, and the trailing `data` is a particle-specific payload (a block
state, a color, ...); see the [Common Toolkit](../common/common.md) for where that
parsing lives.

### `repeat` duplicates the step, not the effect

`repeat: 3` on a step section appends that many identical copies of the whole step,
back to back — a three-step section where the middle one has `repeat: 3` produces
five steps in the final animation. Each copy shares the same timings, so this is "do
this whole step three times in sequence," which is different from `period`, which
ticks a single step repeatedly for the duration of one run. `repeat` never appears as
a regular attribute — it's read and consumed before the step is built.

## Sharp edges

- **An empty step is legal and silently does nothing.** A step section with no
  effect keys — maybe you're staging config for later, or a typo'd key didn't
  register — parses and runs fine, it just has nothing to show for it.
- **Step order comes from your YAML, not a fixed registry order.** Unlike attribute
  keys *within* one step (which apply in a fixed internal order regardless of how you
  arrange them), the steps themselves run in the order their sections appear in the
  file — reordering `charge`/`pull`/`payoff` in the YAML changes playback order.
- **A missing player or location on a context-dependent effect fails silently, not
  loudly.** If `playerTitle` never shows and you didn't touch the config, check that
  you actually passed a `Player` into your `AnimationContext` — the effect itself
  won't tell you it was skipped.
- Everything in [Interactions](interactions.md)' sharp-edges list applies to the
  `interaction`/`interactions` effects embedded here too.

## See also

- [WUtils Animation](../animation/animation.md) — the runtime this builds into:
  `BLOCKING` vs `PARALLEL`, how steps chain, and `Animation#run()`.
- [Interactions](interactions.md) — the `interaction`/`interactions` effects.
- [Common Toolkit](../common/common.md) — the `data` grammar for `worldParticle` and
  other low-level parsing helpers.
- [Ranges, Durations and Values](../common/values.md) — the duration expressions
  used by every timing key.
- [Configurables](configurables.md) — the module overview and dependency table.
- [the contributor page](../../dev/configurables/animations.md) — the attribute
  interfaces and `AnimationStepConfigurable` internals, if you need to go deeper.
