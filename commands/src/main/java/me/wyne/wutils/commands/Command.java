package me.wyne.wutils.commands;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Used to create command that can be easily modified adding different child commands.
 */
public class Command {

    /**
     * This function will be executed if command is called without any args.
     */
    private Function<CommandSender, Boolean> parentCommand;
    private String parentCommandPermission;

    /**
     * Contains children commands that are
     */
    private HashMap<Integer, Pair<List<String>, BiFunction<CommandSender, String[], Boolean>>> childrenCommands = new HashMap<>();
    private Table<Integer, String, List<String>> childrenCommandsPermissions = HashBasedTable.create();

    public void setParentCommand(@NotNull final Function<CommandSender, Boolean> parentCommand)
    {
        this.parentCommand = parentCommand;
    }

    public void setParentCommand(@NotNull final Function<CommandSender, Boolean> parentCommand, @NotNull final String permission)
    {
        this.parentCommand = parentCommand;
        this.parentCommandPermission = permission;
    }

    public void setParentCommandPermission(@NotNull final String permission)
    {
        this.parentCommandPermission = permission;
    }

    public void setChildCommand(final int argIndex, @NotNull final List<String> childCommand, @Nullable final BiFunction<CommandSender, String[], Boolean> executor)
    {
        List<String> newCommands = childrenCommands.containsKey(argIndex) ? childrenCommands.get(argIndex).getKey() : new ArrayList<>();
        newCommands.addAll(childCommand);
        this.childrenCommands.put(argIndex, new ImmutablePair<>(newCommands, executor));
    }

    public void setChildCommand(final int argIndex, @NotNull String childCommand, @Nullable final BiFunction<CommandSender, String[], Boolean> executor, @Nullable final String ... permissions)
    {
        List<String> newCommands = childrenCommands.containsKey(argIndex) ? childrenCommands.get(argIndex).getKey() : new ArrayList<>();
        newCommands.add(childCommand);
        this.childrenCommands.put(argIndex, new ImmutablePair<>(newCommands, executor));
        this.childrenCommandsPermissions.put(argIndex, childCommand, List.of(permissions));
    }

    public void setChildCommandPermissions(final int argIndex, @NotNull final String childCommand, @Nullable final String ... permissions)
    {
        this.childrenCommandsPermissions.put(argIndex, childCommand, List.of(permissions));
    }

    public void setChildrenCommands(@NotNull final HashMap<Integer, Pair<List<String>, BiFunction<CommandSender, String[], Boolean>>> childrenCommands)
    {
        this.childrenCommands = childrenCommands;
    }

    public void setChildrenCommandsPermissions(@NotNull final Table<Integer, String, List<String>> childrenCommandsPermissions)
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
            if (childrenCommands.containsKey(argIndex))
            {
                for (String command : childrenCommands.get(argIndex).getKey())
                {
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
            if (parentCommandPermission != null)
            {
                if (sender.hasPermission(parentCommandPermission))
                    return parentCommand.apply(sender);
            }
            return parentCommand.apply(sender);
        }
        return false;
    }

    public boolean executeChildCommand(@NotNull final CommandSender sender, @NotNull final String args[], final int argIndex, @NotNull final String arg)
    {
        if (childrenCommands.containsKey(argIndex) && childrenCommands.get(argIndex).getValue() != null)
        {
            if (childrenCommands.get(argIndex).getKey().contains(arg))
            {
                if (childrenCommandsPermissions.contains(argIndex, arg))
                {
                    for (String permission : childrenCommandsPermissions.get(argIndex, arg))
                    {
                        if (sender.hasPermission(permission))
                            return childrenCommands.get(argIndex).getValue().apply(sender, args);
                    }
                }
                return childrenCommands.get(argIndex).getValue().apply(sender, args);
            }
        }
        return false;
    }

}
