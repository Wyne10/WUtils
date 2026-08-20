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

`Operation<T extends Number>` (`operation/Operation.java:13-16`) mirrors `Comparator`
but returns a `T` instead of a `Boolean`. `ContainedOperation<T>`
(`operation/ContainedOperation.java:9-11`) mirrors `ContainedComparator`, and
`IntOperation`/`DoubleOperation` are the records, with the same `toString`
round-tripping behaviour (`operation/IntOperation.java:23-25`).

The extra piece is `Operable<T>` (`operation/Operable.java:12-21`), which the
comparator side has no equivalent of. It is the arithmetic backend: five methods —
`add`, `subtract`, `multiply`, `divide`, `power` — implemented once per numeric type by
`IntOperations` (`operation/IntOperations.java:11`) and `DoubleOperations`
(`operation/DoubleOperations.java:11`). The operation classes are dispatchers that pick
a backend and call one method on it, e.g. `Divide.evaluate` is
`Operations.getOperations(leftOperand).divide(leftOperand, rightOperand)`
(`operation/Divide.java:12`). This indirection is why the same `Plus<T>` works for both
`Integer` and `Double`.

**`Set` is not arithmetic.** `Set.evaluate` returns its right operand and ignores the
left (`operation/Set.java:12-14`) — it is assignment. It is also the silent fallback
for an unrecognised operator, which means a typo in a config file does not fail; it
overwrites the value instead.

### Divide and power behave differently per type

| Case | `IntOperations` | `DoubleOperations` |
|---|---|---|
| `divide` | integer division, truncating toward zero (`operation/IntOperations.java:27`) | IEEE division (`operation/DoubleOperations.java:27`) |
| divide by zero | throws `ArithmeticException` | returns `Infinity` or `NaN`, no exception |
| `power` | `(int) Math.pow(base, exponent)` (`operation/IntOperations.java:32`) | `Math.pow` (`operation/DoubleOperations.java:32`) |
| negative exponent | `Math.pow` gives a fraction, the cast truncates it to `0` (or `±1` for base ±1) | correct fractional result |

The integer `power` truncation is easy to hit: `2 ** -1` yields `0`, silently.

## Lookup behaviour — inconsistent on purpose or not, know it

Both packages expose a static factory. Their miss behaviour differs by method:

| Call | On unknown/null input |
|---|---|
| `Comparators.getComparator(String)` (`comparator/Comparators.java:22-33`) | returns `Equals` — silent default |
| `Comparators.getOperator(Comparator)` (`comparator/Comparators.java:37-46`) | returns `""` |
| `Operations.getOperation(String)` (`operation/Operations.java:40-51`) | returns `Set` — silent default |
| `Operations.getOperator(Operation)` (`operation/Operations.java:56-66`) | returns `""` |
| `Operations.getOperations(T)` (`operation/Operations.java:27-35`) | **throws** `IllegalArgumentException("Unknown operable type")` |

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
(`operation/Operations.java:72-78`, `operation/Operations.java:86-92`).

The accepted syntax is an optional operator followed by a number:
`COMPARATOR_REGEX` is `(<=|>=|==|<|>)?(-?\d+(?:\.\d+)?)` and `OPERATION_REGEX` is
`(\+|-|\*|/|\*\*)?(-?\d+(?:\.\d+)?)` (`operation/Operations.java:17`). Omitting the
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
