package com.nextdevv.benders_application_plugin.commands;

import java.util.List;

/**
 * Interface for commands
 */
public interface ICommand {
    String getName();
    String getDescription();
    String getUsage();
    List<String> complete(CommandContext context);
    void execute(CommandContext context);
}
