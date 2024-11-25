package com.nextdevv.benders_application_plugin.commands;

import com.nextdevv.benders_application_plugin.commands.sub.CreateCommand;
import com.nextdevv.benders_application_plugin.commands.sub.GuideCommand;
import com.nextdevv.benders_application_plugin.utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor, TabExecutor {
    private List<ICommand> commands = new ArrayList<>();

    public CommandManager() {
        commands.add(new CreateCommand());
        commands.add(new GuideCommand());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(args.length == 0) {
            commandSender.sendMessage(ChatUtil.color("&cUsage: /mysticchests <subcommand>"));
            return true;
        }

        for(ICommand cmd : commands) {
            if(cmd.getName().equalsIgnoreCase(args[0])) {
                cmd.execute(new CommandContext(commandSender, args));
                return true;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> completions = new ArrayList<>();

        if(args.length == 1) {
            for(ICommand cmd : commands) {
                completions.add(cmd.getName());
            }
        }

        if (args.length > 1) {
            for(ICommand cmd : commands) {
                if(cmd.getName().equalsIgnoreCase(args[0])) {
                    completions.addAll(cmd.complete(new CommandContext(commandSender, args)));
                }
            }
        }

        return completions;
    }
}
