package me.wyne.wutils.config;

import java.lang.reflect.Field;

public record ConfigField(Object holder, Field field, String path, String value, String comment) {

    public ConfigField(Object holder, Field field, String path, String value, String comment) {
        this.holder = holder;
        this.field = field;
        this.path = path;
        this.value = value;
        this.comment = comment;
    }

    public String generateConfigLine()
    {
        StringBuilder stringBuilder = new StringBuilder();
        if (!comment.isEmpty())
        {
            stringBuilder.append("# ");
            stringBuilder.append(comment);
            stringBuilder.append("\n");
        }
        stringBuilder.append(path);
        stringBuilder.append(": ");
        stringBuilder.append(value);
        return stringBuilder.toString();
    }
}
