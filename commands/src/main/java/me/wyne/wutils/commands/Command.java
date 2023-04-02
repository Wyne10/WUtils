package me.wyne.wutils.commands;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Used to create command that can be easily modified adding different child commands.
 */
public class Command {

    /**
     * This {@link Function} will be executed if command is called without any args.
     */
    private Function<CommandSender, Boolean> parentCommand;
    /**
     * If player has at least one of this permissions then parent command will be executed.
     */
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
        this.parentCommandPermissions = permissions != null ? Set.of(permissions) : new HashSet<>();
    }

    public void setParentCommandPermissions(@Nullable final String ... permissions)
    {
        this.parentCommandPermissions = permissions != null ? Set.of(permissions) : new HashSet<>();
    }

    public void setChildCommand(final int argIndex, @NotNull final String childCommand, @Nullable final BiFunction<CommandSender, String[], Boolean> executor, @Nullable final String ... permissions)
    {
        this.childrenCommands.put(argIndex, childCommand, executor != null ? executor : (t, u) -> false);
        this.childrenCommandsPermissions.put(argIndex, childCommand, permissions != null ? Set.of(permissions) : new HashSet<>());
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

        int argIndex = 0;
        for (String arg : args)
        {
            if (!childrenCommands.containsRow(argIndex))
            {
                argIndex++;
                continue;
            }

            for (String command : childrenCommands.columnKeySet())
            {
                if (!childrenCommands.contains(argIndex, command))
                    continue;

                if (childrenCommandsPermissions.contains(argIndex, command))
                {
                    for (String permission : childrenCommandsPermissions.get(argIndex, command))
                    {
                        if (sender.hasPermission(permission))
                            result.add(command);
                    }
                }
                else
                {
                    result.add(command);
                }
            }
            argIndex++;
        }

        return result;
    }

    public boolean executeCommand(@NotNull final CommandSender sender, @NotNull final String[] args)
    {
        if (args.length == 0)
            executeParentCommand(sender);

        int argIndex = 0;
        for (String arg : args)
        {
            executeChildCommand(sender, args, argIndex, arg);
            argIndex++;
        }

        return false;
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
