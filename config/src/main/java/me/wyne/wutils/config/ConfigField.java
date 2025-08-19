package me.wyne.wutils.config;

import java.lang.reflect.Field;

public record ConfigField(Object holder, Field field, String path, String value, String comment, boolean load) {

    public ConfigField(Object holder, Field field, String path, String value, String comment, boolean load) {
        this.holder = holder;
        this.field = field;
        this.path = path;
        this.value = value;
        this.comment = comment;
        this.load = load;
    }

    public String generateConfigLine() {
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
