package me.wyne.wutils.commands;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Command {

    private Function<CommandSender, Boolean> parentCommand;
    private Set<String> parentCommandPermissions = new HashSet<>();

    private Table<Integer, String, BiFunction<CommandSender, String[], Boolean>> childrenCommands = HashBasedTable.create();
    private Table<Integer, String, Set<String>> childrenCommandsPermissions = HashBasedTable.create();

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

    public void setChildCommand(final int argIndex, @Nullable final String childCommand, @Nullable final BiFunction<CommandSender, String[], Boolean> executor, @Nullable final String ... permissions)
    {
        this.childrenCommands.put(argIndex, childCommand != null ? childCommand : "", executor != null ? executor : (t, u) -> false);
        Set<String> newPermissions = childrenCommandsPermissions.contains(argIndex, childCommand != null ? childCommand : "") ? childrenCommandsPermissions.get(argIndex, childCommand != null ? childCommand : "") : new HashSet<>();
        newPermissions.addAll(permissions != null ? Set.of(permissions) : new HashSet<>());
        this.childrenCommandsPermissions.put(argIndex, childCommand != null ? childCommand : "", newPermissions);
    }

    public void setChildCommands(final int argIndex, @NotNull final Set<String> childCommands, @Nullable final BiFunction<CommandSender, String[], Boolean> executor, @Nullable final String ... permissions)
    {
        for (String childCommand : childCommands)
        {
            this.childrenCommands.put(argIndex, childCommand, executor != null ? executor : (t, u) -> false);
            Set<String> newPermissions = childrenCommandsPermissions.contains(argIndex, childCommand) ? childrenCommandsPermissions.get(argIndex, childCommand) : new HashSet<>();
            newPermissions.addAll(permissions != null ? Set.of(permissions) : new HashSet<>());
            this.childrenCommandsPermissions.put(argIndex, childCommand, newPermissions);
        }
    }

    public void setChildCommand(final int argIndex, @Nullable final String childCommand, @Nullable final String ... permissions)
    {
        this.childrenCommands.put(argIndex, childCommand != null ? childCommand : "", childrenCommands.contains(argIndex, childCommand != null ? childCommand : "") ? childrenCommands.get(argIndex, childCommand != null ? childCommand : "") : (t, u) -> false);
        Set<String> newPermissions = childrenCommandsPermissions.contains(argIndex, childCommand != null ? childCommand : "") ? childrenCommandsPermissions.get(argIndex, childCommand != null ? childCommand : "") : new HashSet<>();
        newPermissions.addAll(permissions != null ? Set.of(permissions) : new HashSet<>());
        this.childrenCommandsPermissions.put(argIndex, childCommand != null ? childCommand : "", newPermissions);
    }

    public void setChildCommands(final int argIndex, @NotNull final Set<String> childCommands, @Nullable final String ... permissions)
    {
        for (String childCommand : childCommands)
        {
            this.childrenCommands.put(argIndex, childCommand, childrenCommands.contains(argIndex, childCommand) ? childrenCommands.get(argIndex, childCommand) : (t, u) -> false);
            Set<String> newPermissions = childrenCommandsPermissions.contains(argIndex, childCommand) ? childrenCommandsPermissions.get(argIndex, childCommand) : new HashSet<>();
            newPermissions.addAll(permissions != null ? Set.of(permissions) : new HashSet<>());
            this.childrenCommandsPermissions.put(argIndex, childCommand, newPermissions);
        }
    }

    public void setChildCommandPermissions(final int argIndex, @NotNull final String childCommand, @Nullable final String ... permissions)
    {
        this.childrenCommandsPermissions.put(argIndex, childCommand, permissions != null ? Set.of(permissions) : new HashSet<>());
    }

    public void setChildrenCommands(@NotNull final Table<Integer, String, BiFunction<CommandSender, String[], Boolean>> childrenCommands)
    {
        this.childrenCommands = childrenCommands;
    }

    public void setChildrenCommandsPermissions(@NotNull final Table<Integer, String, Set<String>> childrenCommandsPermissions)
    {
        this.childrenCommandsPermissions = childrenCommandsPermissions;
    }

    @NotNull
    public List<String> tabComplete(@NotNull final CommandSender sender, @NotNull final String[] args)
    {
        List<String> result = new ArrayList<>();

        int argIndex = args.length - 1;

        if (!childrenCommands.containsRow(argIndex))
            return result;

        for (String command : childrenCommands.columnKeySet())
        {
            if (command.isBlank())
                continue;
            if (!childrenCommands.contains(argIndex, command))
                continue;

            if (childrenCommandsPermissions.contains(argIndex, command))
            {
                for (String permission : childrenCommandsPermissions.get(argIndex, command))
                {
                    if (sender.hasPermission(permission))
                        result.add(command.replace("+", ""));
                }
            }
            else
            {
                result.add(command.replace("+", ""));
            }
        }

        return result;
    }

    public boolean executeCommand(@NotNull final CommandSender sender, @NotNull final String[] args)
    {
        if (args.length == 0)
            return executeParentCommand(sender);

        if (args.length >= 2)
        {
            if (!childrenCommands.contains(args.length - 1, args[args.length - 1]))
            {
                if (childrenCommands.contains(args.length - 2, args[args.length - 2] + "+"))
                {
                    return executeChildCommand(sender, args, args.length - 2, args[args.length - 2] + "+");
                }
            }
        }

        if (!childrenCommands.contains(args.length - 1, args[args.length - 1]))
            return executeChildCommand(sender, args, args.length - 1, "");
        else
            return executeChildCommand(sender, args, args.length - 1, args[args.length - 1]);
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

    public boolean executeChildCommand(@NotNull final CommandSender sender, @NotNull final String args[], final int argIndex, @NotNull final String arg)
    {
        if (!childrenCommands.contains(argIndex, arg))
            return false;

        if (!childrenCommandsPermissions.get(argIndex, arg).isEmpty())
        {
            for (String permission : childrenCommandsPermissions.get(argIndex, arg))
            {
                if (sender.hasPermission(permission))
                    return childrenCommands.get(argIndex, arg).apply(sender, args);
            }
        }
        else
            return childrenCommands.get(argIndex, arg).apply(sender, args);

        return false;
    }

}
