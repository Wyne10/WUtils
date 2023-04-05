package me.wyne.wutils.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Command {

    private Function<CommandSender, Boolean> parentCommand;
    private Set<String> parentCommandPermissions = new HashSet<>();

    private Map<String, BiFunction<CommandSender, String[], Boolean>> childrenCommands = new HashMap<>();
    private Map<String, Set<String>> childrenCommandsPermissions = new HashMap<>();

    public void setParentCommand(@Nullable final Function<CommandSender, Boolean> parentCommand)
    {
        this.parentCommand = parentCommand;
    }

    public void setParentCommand(@Nullable final Function<CommandSender, Boolean> parentCommand, @Nullable final String ... permissions)
    {
        this.parentCommand = parentCommand;
        Set<String> newPermissions = parentCommandPermissions;
        newPermissions.addAll(permissions != null ? Set.of(permissions) : new HashSet<>());
        this.parentCommandPermissions = newPermissions;
    }

    public void setParentCommandPermissions(@Nullable final String ... permissions)
    {
        this.parentCommandPermissions = permissions != null ? Set.of(permissions) : new HashSet<>();
    }

    public void setChildCommand(@NotNull final String childCommandPattern, @Nullable final BiFunction<CommandSender, String[], Boolean> executor, @Nullable final String ... permissions)
    {
        this.childrenCommands.put(childCommandPattern, executor != null ? executor : (t, u) -> false);
        Set<String> newPermissions = childrenCommandsPermissions.containsKey(childCommandPattern) ? childrenCommandsPermissions.get(childCommandPattern) : new HashSet<>();
        newPermissions.addAll(permissions != null ? Set.of(permissions) : new HashSet<>());
        this.childrenCommandsPermissions.put(childCommandPattern, newPermissions);
    }

    public void setChildCommand(@NotNull final String childCommandPattern, @Nullable final String ... permissions)
    {
        this.childrenCommands.put(childCommandPattern, childrenCommands.containsKey(childCommandPattern) ? childrenCommands.get(childCommandPattern) : (t, u) -> false);
        Set<String> newPermissions = childrenCommandsPermissions.containsKey(childCommandPattern) ? childrenCommandsPermissions.get(childCommandPattern) : new HashSet<>();
        newPermissions.addAll(permissions != null ? Set.of(permissions) : new HashSet<>());
        this.childrenCommandsPermissions.put(childCommandPattern, newPermissions);
    }

    public void setChildCommandPermissions(@NotNull final String childCommandPattern, @Nullable final String ... permissions)
    {
        this.childrenCommandsPermissions.put(childCommandPattern, permissions != null ? Set.of(permissions) : new HashSet<>());
    }

    public void setChildrenCommands(@NotNull final HashMap<String, BiFunction<CommandSender, String[], Boolean>> childrenCommands)
    {
        this.childrenCommands = childrenCommands;
    }

    public void setChildrenCommandsPermissions(@NotNull final HashMap<String, Set<String>> childrenCommandsPermissions)
    {
        this.childrenCommandsPermissions = childrenCommandsPermissions;
    }

    public String arrayToPattern(@NotNull final String[] arr)
    {
        Set<String> keys = new HashSet<>();
        StringBuilder result = new StringBuilder();

        for (String pattern : childrenCommands.keySet())
        {
            keys.addAll(List.of(pattern.split("\\s+")));
        }

        for (String str : arr)
        {
            if (keys.contains(str))
                result.append(" ").append(str);
            else
                result.append(" ").append("<any>");
        }

        return result.toString().strip();
    }

    @NotNull
    public List<String> tabComplete(@NotNull final CommandSender sender, @NotNull final String[] args)
    {
        List<String> result = new ArrayList<>();

        if (args.length == 0)
            return result;

        for (String pattern : childrenCommands.keySet())
        {
            String[] splitPattern;
            if ((splitPattern = pattern.split("\\s+")).length < args.length)
                continue;
            if (splitPattern[args.length - 1].equalsIgnoreCase("<any>"))
                continue;

            if (childrenCommandsPermissions.containsKey(pattern))
            {
                for (String permission : childrenCommandsPermissions.get(pattern))
                {
                    if (sender.hasPermission(permission))
                        result.add(splitPattern[args.length - 1]);
                }
            }
            else
            {
                result.add(splitPattern[args.length - 1]);
            }
        }

        return result;
    }

    public boolean executeCommand(@NotNull final CommandSender sender, @NotNull final String[] args)
    {
        if (args.length == 0)
            return executeParentCommand(sender);

        return executeChildCommand(sender, args);
    }

    public boolean executeParentCommand(@NotNull final CommandSender sender)
    {
        if (parentCommand != null)
        {
            if (!parentCommandPermissions.isEmpty())
            {
                for (String permission : parentCommandPermissions)
                {
                    if (sender.hasPermission(permission))
                        return parentCommand.apply(sender);
                }
            }
            else
                return parentCommand.apply(sender);
        }
        return false;
    }

    public boolean executeChildCommand(@NotNull final CommandSender sender, @NotNull final String args[])
    {
        String pattern = arrayToPattern(args);

        if (!childrenCommands.containsKey(pattern))
            return false;

        if (childrenCommandsPermissions.containsKey(pattern))
        {
            for (String permission : childrenCommandsPermissions.get(pattern))
            {
                if (sender.hasPermission(permission))
                    return childrenCommands.get(pattern).apply(sender, args);
            }
        }
        else
            return childrenCommands.get(pattern).apply(sender, args);

        return false;
    }

}
