package me.wyne.wutils.structure.modifier.edit;

import me.wyne.wutils.common.Args;
import me.wyne.wutils.common.operation.IntOperation;
import me.wyne.wutils.common.operation.Operations;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Config value for {@link GrowEditModifier}, parsed by {@link #parse} from a single
 * space-delimited string: {@code margin(5) strength(2) base(+0) [direction] [mask]}.
 *
 * <p>{@code direction} and {@code mask} are both optional and share one token slot: the third
 * token is tried as a direction keyword ({@code up}/{@code raise}, {@code down}/{@code lower},
 * {@code both}/{@code slope}); if it does not match one, it is taken as the mask instead and
 * {@code direction} defaults to {@link Direction#BOTH}. A fourth token, present only when the
 * third was a direction keyword, supplies the mask.</p>
 */
public record GrowSettings(int margin, double strength, @NotNull IntOperation base,
                           @NotNull Direction direction, @Nullable String mask) {

    public enum Direction {
        UP, DOWN, BOTH
    }

    public static @NotNull GrowSettings parse(@NotNull String input) {
        var args = new Args(input, Args.SPACE_DELIMITER);
        int margin = Integer.parseInt(args.get(0, "5"));
        double strength = Double.parseDouble(args.get(1, "2"));
        IntOperation base = Operations.getIntOperation(args.get(2, "+0"));

        // Token 3 may be a direction keyword or, for brevity, the mask (direction then defaults).
        Direction direction = Direction.BOTH;
        String mask = null;
        String token = args.getNullable(3);
        if (token != null) {
            Direction parsedDirection = parseDirection(token);
            if (parsedDirection != null) {
                direction = parsedDirection;
                mask = args.getNullable(4);
            } else {
                mask = token;
            }
        }
        return new GrowSettings(margin, strength, base, direction, mask);
    }

    private static @Nullable Direction parseDirection(@NotNull String token) {
        return switch (token.toLowerCase()) {
            case "up", "raise" -> Direction.UP;
            case "down", "lower" -> Direction.DOWN;
            case "both", "slope" -> Direction.BOTH;
            default -> null;
        };
    }

    @Override
    public @NotNull String toString() {
        String result = margin + " " + strength + " " + base + " " + direction.name().toLowerCase();
        return mask == null ? result : result + " " + mask;
    }
}