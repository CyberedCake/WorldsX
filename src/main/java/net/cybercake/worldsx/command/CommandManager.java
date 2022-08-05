package net.cybercake.worldsx.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.cybercake.cyberapi.dependencies.annotations.jetbrains.NotNull;
import net.cybercake.cyberapi.spigot.chat.TabCompleteType;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.server.commands.Command;
import net.cybercake.cyberapi.spigot.server.commands.CommandInformation;
import net.cybercake.worldsx.Main;
import net.cybercake.worldsx.command.subcommands.Help;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class CommandManager extends Command {

    public static CommandManager INSTANCE = null;
    public static final String PERMISSION_ROOT = "worldsx.command.";
    public static final String NO_PERMISSION_MESSAGE = "&cYou don't have permission to use this!";
    public static final String SUB_COMMANDS_PACKAGE = "net.cybercake.worldsx.command.subcommands";

    public CommandManager() {
        super(
                newCommand("worldsx")
                        .setDescription("The main command for the Updater plugin!")
                        .setUsage("Use &e/updater help &rfor help!")
                        .setAliases("worlds", "world", "w", "dimensions")
                        .setTabCompleteType(TabCompleteType.SEARCH)
                        .setCommodore(Commodore.forCommand())
        );
        INSTANCE = this;

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), SubCommand::registerAll, 50L);
    }

    @Override
    public boolean perform(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        List<SubCommand> subCommandsForUser = SubCommand.getSubCommandsFor(sender);
        if(!sender.hasPermission(PERMISSION_ROOT + "*") && subCommandsForUser.size() < 2)
            sender.sendMessage(UChat.chat(NO_PERMISSION_MESSAGE));
        else if(args.length < 1) {
            SubCommand helpCommand = SubCommand.getSubCommandClass(Help.class);

            if (helpCommand == null) sender.sendMessage(UChat.chat("&cAn unknown error has occurred!"));
            else helpCommand.perform(sender, command, "/" + command + " " + helpCommand.getUsage(), args);
        }else{
            SubCommand attemptingToRun = SubCommand.getSubCommandNameOrAlias(args[0], false);
            if(attemptingToRun == null) {
                sender.sendMessage(UChat.chat("&cUnknown sub-command: &8" + args[0])); return true;
            }
            if(!sender.hasPermission(PERMISSION_ROOT + "*") && !subCommandsForUser.contains(attemptingToRun)) {
                sender.sendMessage(UChat.chat(NO_PERMISSION_MESSAGE)); return true;
            }
            attemptingToRun.perform(sender, command, "/" + command + " " + attemptingToRun.getUsage(), Arrays.copyOfRange(args, 1, args.length));
        }

        return true;

    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        List<SubCommand> subCommandsForUser = SubCommand.getSubCommandsFor(sender);
        if(!sender.hasPermission(PERMISSION_ROOT + "*") && subCommandsForUser.size() < 2) return null;

        if(args.length == 1) return subCommandsForUser
                .stream()
                .map(SubCommand::getName)
                .toList();
        if(args.length >= 1) {
            SubCommand attemptingToTabComplete = SubCommand.getSubCommandNameOrAlias(args[0], false);
            if(attemptingToTabComplete == null) return null;
            if(!sender.hasPermission(PERMISSION_ROOT + "*") && !subCommandsForUser.contains(attemptingToTabComplete)) return null;
            return attemptingToTabComplete.tab(sender, command, "/" + command + " " + attemptingToTabComplete.getUsage(), Arrays.copyOfRange(args, 1, args.length));
        }
        return null;
    }

}
