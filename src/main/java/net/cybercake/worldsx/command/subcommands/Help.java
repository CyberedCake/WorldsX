package net.cybercake.worldsx.command.subcommands;

import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.worldsx.Main;
import net.cybercake.worldsx.command.CommandManager;
import net.cybercake.worldsx.command.SubCommand;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class Help extends SubCommand {

    public Help() {
        super(
                "help", null, Main.getLang().getTranslation("commands.help.description"), "help", "?", "info"
        );
    }

    @Override
    public void perform(CommandSender sender, String command, String usage, String[] args) {
        List<SubCommand> subCommandsForUser = SubCommand.getSubCommandsFor(sender);
        sender.sendMessage(UChat.getSeparator(ChatColor.BLUE));
        sender.sendMessage(Main.getLang().getTranslation("commands.help.title", subCommandsForUser.size()));

        for(SubCommand subCommand : subCommandsForUser) {
            String commandFormatted = Main.getLang().getTranslation("commands.help.commandUsage", command + " " + subCommand.getUsage());

            BaseComponent component = new TextComponent(Main.getLang().getTranslation("commands.help.commandShown", command + " " + subCommand.getUsage()));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                    UChat.chat("" +
                            "&bCommand&3: " + commandFormatted + "\n" +
                            "&bDescription&3: &f" + subCommand.getDescription() + "\n" +
                            (Arrays.stream(subCommand.getAliases()).toList().size() != 0
                                    ? "&bAliases&3: \n" +
                                    String.join("\n",
                                            (Arrays.stream(subCommand.getAliases()).map(alias -> "    &3/&f" + command + " " + alias)).toList()
                                    )
                                    : ""
                            ) + "" +
                            (subCommand.getPermission() != null
                                    ? "\n&bPermission&3: &f" + CommandManager.PERMISSION_ROOT + subCommand.getPermission()
                                    : ""
                            )
                    )
            )));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatColor.stripColor(commandFormatted)));

            sender.spigot().sendMessage(component);

            // I had to convert from adventure api to bungee api, and it wasn't as bad as I thought it was gonna be lol
        }

        sender.sendMessage(UChat.getSeparator(ChatColor.BLUE));
    }

    @Override
    public List<String> tab(CommandSender sender, String command, String usage, String[] args) {
        return null;
    }
}