package com.nextdevv.benders_application_plugin.commands;

import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Context for commands.
 *
 * @param sender the sender of the command
 * @param args the command arguments
 */
public record CommandContext(CommandSender sender, String[] args) {
    public CommandContext(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = Arrays.stream(args).toList().subList(1, args.length).toArray(new String[0]);
    }

    /**
     * Gets the command argument at the specified index.
     *
     * @param index the index of the argument to retrieve
     * @return the command argument at the specified index
     */
    public String getArgs(int index) {
        return args[index];
    }
}
