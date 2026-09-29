---
description: >-
  Two matching miniature strategy libraries, so a config string can express a
  comparison or an arithmetic operation.
---

# Comparators and Operations

`me.wyne.wutils.common.comparator` and `me.wyne.wutils.common.operation` are two
matching miniature strategy libraries. Both exist for the same reason: so a config
file can express a *rule* rather than just a value — `>= 5` as a condition, `*2` as a
transformation — and the plugin can parse it once and apply it many times.

Neither package is worth reading class by class. Read the parse entry point, the
lookup table, and the failure modes.

## Comparators — the shape

`Comparator<T>` (`comparator/Comparator.java:11-14`) has one method:
`compare(Comparable<T> leftOperand, T rightOperand)` returning a `Boolean`.

Operand order is consistent and reads naturally left to right: **the first argument is
the value under test, the second is the threshold.** Every implementation is one line
delegating to `compareTo`:

| Class | Meaning | Body |
|---|---|---|
| `Equals` | `left == right` | `leftOperand.compareTo(rightOperand) == 0` (`Equals.java:11`) |
| `GreaterThan` | `left > right` | `compareTo(...) > 0` (`GreaterThan.java:11`) |
| `GreaterOrEqual` | `left >= right` | `compareTo(...) >= 0` (`GreaterOrEqual.java:11`) |
| `LessThan` | `left < right` | `compareTo(...) < 0` (`LessThan.java:11`) |
| `LessOrEqual` | `left <= right` | `compareTo(...) <= 0` (`LessOrEqual.java:11`) |

`ContainedComparator<T>` (`comparator/ContainedComparator.java:9-11`) is the
abstraction seam: it extends `Comparator<T>` and adds a one-argument
`compare(T leftOperand)`. "Contained" means *the threshold is already inside* — the
comparator has been paired with its right operand, so a caller supplies only the value
to test. That is what makes a parsed rule storable and reusable.

`IntComparator` (`comparator/IntComparator.java:11`) and `DoubleComparator`
(`comparator/DoubleComparator.java:11`) are the two concrete containers, both records
holding a `rightOperand` and a `Comparator`. Their `toString` reconstructs the original
config text by asking `Comparators.getOperator` for the symbol and appending the
operand (`comparator/IntComparator.java:23-25`), so a parsed rule round-trips back to
something like `>=5`.

## Operations — the same shape, one method wider

`Operation<T extends Number>` (`operation/Operation.java:16-18`) mirrors `Comparator`
but returns a `T` instead of a `Boolean`. `ContainedOperation<T>`
(`operation/ContainedOperation.java:10-12`) mirrors `ContainedComparator`, and
`IntOperation`/`DoubleOperation` are the records, with the same `toString`
round-tripping behaviour (`operation/IntOperation.java:22-24`).

The extra piece is `Operable<T>` (`operation/Operable.java:21-30`), which the
comparator side has no equivalent of. It is the arithmetic backend: eight methods —
`add`, `subtract`, `multiply`, `divide`, `power`, `modulo`, `min`, `max` — implemented
once per numeric type by `IntOperations` (`operation/IntOperations.java:12`) and
`DoubleOperations` (`operation/DoubleOperations.java:12`). The operation classes are
dispatchers that pick a backend and call one method on it, e.g. `Divide.evaluate` is
`Operations.getOperations(leftOperand).divide(leftOperand, rightOperand)`
(`operation/Divide.java:13`). This indirection is why the same `Plus<T>` works for both
`Integer` and `Double`.

**`Set` is not arithmetic.** `Set.evaluate` returns its right operand and ignores the
left (`operation/Set.java:12-14`) — it is assignment. It is also the silent fallback
for an unrecognised operator, which means a typo in a config file does not fail; it
overwrites the value instead.

**`Min` and `Max` are not arithmetic either.** They select an operand rather than
combining the two (`operation/Min.java:13-15`, `operation/Max.java:13-15`), which makes
them clamps: `<10` caps a value at ten, `>0` floors it at zero. The naming reads
backwards at a glance — the class that caps is `Min`, because capping *is* taking the
minimum of the value and the bound — so `<` is `Min` and `>` is `Max`, not the other way
round.

### `<` and `>` mean opposite things in the two packages

This is the one genuinely dangerous thing about the new operators. Both packages now
claim `<` and `>`:

| String | Read by `Comparators` | Read by `Operations` |
|---|---|---|
| `<10` | `LessThan(10)` — a test, "is it below ten?" | `Min(10)` — a transform, "cap it at ten" |
| `>0` | `GreaterThan(0)` — "is it above zero?" | `Max(0)` — "floor it at zero" |

Nothing in either parser detects that a string was meant for the other one. A config key
documented as a comparator but read with `getIntOperation` silently becomes a clamp, and
returns a number where the caller expected a decision. Keep comparator keys and operation
keys clearly separated in the config schema, and never route one string through both.

### Divide, modulo and power behave differently per type

| Case | `IntOperations` | `DoubleOperations` |
|---|---|---|
| `divide` | integer division, truncating toward zero (`operation/IntOperations.java:30`) | IEEE division (`operation/DoubleOperations.java:30`) |
| divide by zero | throws `ArithmeticException` | returns `Infinity` or `NaN`, no exception |
| `modulo` | `%` on `int` (`operation/IntOperations.java:40`) | `%` on `double` (`operation/DoubleOperations.java:40`) |
| modulo by zero | throws `ArithmeticException` | returns `NaN`, no exception |
| `power` | `(int) Math.pow(base, exponent)` (`operation/IntOperations.java:35`) | `Math.pow` (`operation/DoubleOperations.java:35`) |
| negative exponent | `Math.pow` gives a fraction, the cast truncates it to `0` (or `±1` for base ±1) | correct fractional result |

The integer `power` truncation is easy to hit: `2 ** -1` yields `0`, silently.

`modulo` is Java's `%`, which is a **remainder, not a mathematical modulo**: the sign of
the result follows the dividend, so `-7 % 3` is `-1`, not `2`. Anything cycling a value
into a fixed bucket range — a rotation, an index into a list, a wrapped coordinate — will
produce a negative result for a negative input and index out of bounds. `min` and `max`
are `Math.min`/`Math.max` in both backends (`operation/IntOperations.java:45`,
`operation/IntOperations.java:50`), so the `double` versions propagate `NaN` and order
`-0.0` below `0.0`.

## Lookup behaviour — inconsistent on purpose or not, know it

Both packages expose a static factory. Their miss behaviour differs by method:

| Call | On unknown/null input |
|---|---|
| `Comparators.getComparator(String)` (`comparator/Comparators.java:22-33`) | returns `Equals` — silent default |
| `Comparators.getOperator(Comparator)` (`comparator/Comparators.java:37-46`) | returns `""` |
| `Operations.getOperation(String)` (`operation/Operations.java:47-60`) | returns `Set` — silent default |
| `Operations.getOperator(Operation)` (`operation/Operations.java:66-77`) | returns `""` |
| `Operations.getOperations(T)` (`operation/Operations.java:28-35`) | **throws** `IllegalArgumentException("Unknown operable type")` |

`getOperations` is the only one that throws. It accepts any `Number` but supports only
`Integer` and `Double`, so a `Long`, `Float` or `BigDecimal` fails at runtime — and it
fails inside whichever `Operation` dispatched to it, not at the call site that supplied
the number.

Note also that `getComparator` returning `Equals` for an unknown operator is how `==`
works at all: `COMPARATOR_REGEX` matches `==` (`comparator/Comparators.java:16`), but
the switch has no `==` branch and falls through to the default.

## Parsing config text

Four methods turn a string into a contained rule:
`getIntComparator`/`getDoubleComparator` (`comparator/Comparators.java:52-58`,
`comparator/Comparators.java:66-72`) and `getIntOperation`/`getDoubleOperation`
(`operation/Operations.java:85-91`, `operation/Operations.java:99-105`).

The accepted syntax is an optional operator followed by a number:
`COMPARATOR_REGEX` is `(<=|>=|==|<|>)?(-?\d+(?:\.\d+)?)` and `OPERATION_REGEX` is
`(\+|-|\*|/|\*\*|%|<|>)?(-?\d+(?:\.\d+)?)` (`operation/Operations.java:18`). Omitting the
operator is legal and yields the default — `Equals` for comparators, `Set` for
operations. So a bare `5` in a config means "equal to 5" as a condition and "set to 5"
as a transformation.

**All four discard the result of `matches()`.** The pattern is matched and the boolean
thrown away (`comparator/Comparators.java:54`), then `matcher.group(1)` is called
regardless. For a string the regex does not match, `group` throws
`IllegalStateException("No match found")` rather than any kind of parse error. A
malformed config value therefore produces a confusing exception from deep inside the
regex API, not a message naming the bad value. Validate input before calling these, or
be ready to catch `IllegalStateException` alongside `NumberFormatException`.

## See also

- [WUtils Common](common.md) — module overview and the nullability contract.
- [Ranges](ranges.md) — the other "value expressed in config" abstraction in this
  module.
- [Config Utilities](config-utils.md) — reading these strings out of a section.
