package me.wyne.wutils.config;

public record ConfigField(String path, String value, String comment) {

    public ConfigField(String path, String value, String comment) {
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
