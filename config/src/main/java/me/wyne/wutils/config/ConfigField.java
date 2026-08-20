package me.wyne.wutils.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * A single {@link ConfigEntry}-annotated field resolved via reflection, along with its rendered YAML
 * value and the metadata needed to generate and reload its config line.
 *
 * <p>{@code value} is {@code @Nullable} because {@link ConfigFieldParser#getConfigField} can leave it
 * unset if reading the field via reflection fails; a null value results in a generated line whose value
 * is the literal text {@code null}.</p>
 */
public record ConfigField(@NotNull Object holder, @NotNull Field field, @NotNull String path, @Nullable String value, @NotNull String comment, boolean load) {

    public ConfigField(@NotNull Object holder, @NotNull Field field, @NotNull String path, @Nullable String value, @NotNull String comment, boolean load) {
        this.holder = holder;
        this.field = field;
        this.path = path;
        this.value = value;
        this.comment = comment;
        this.load = load;
    }

    /**
     * Renders this field's YAML line, including its comment (if any) and quoting the value if the
     * field's declared type is {@link String}.
     */
    public @NotNull String generateConfigLine() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!comment.isEmpty()) {
            stringBuilder.append("  ").append("# ").append(comment).append("\n");
        }
        stringBuilder.append("  ").append(path.substring(path.lastIndexOf('.') + 1));
        stringBuilder.append(": ");
        if (field.getType() == String.class) {
            stringBuilder.append("'").append(value).append("'");
        } else
            stringBuilder.append(value);
        return stringBuilder.toString();
    }
}
