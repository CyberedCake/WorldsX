package net.cybercake.worldsx.command.subcommands;

import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.worldsx.Main;
import net.cybercake.worldsx.command.SubCommand;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class List extends SubCommand {

    public List() {
        super("list", "list", Main.getLang().getTranslation("commands.list.description"), "list [world]", "worldlist");
    }

    @Override
    public void perform(CommandSender sender, String command, String usage, String[] args) {
        sender.sendMessage(UChat.getSeparator(ChatColor.BLUE));
        sender.sendMessage(Main.getLang().getTranslation("commands.list.title", Bukkit.getWorlds().size()));
        for(World world : Bukkit.getWorlds()) {
            java.util.List<String> players = new ArrayList<>(
                    Main.getInstance().getOnlinePlayers()
                            .stream()
                            .filter(player -> player.getWorld().equals(world))
                            .map(Player::getName)
                            .toList()
            );

            BaseComponent component = new TextComponent(Main.getLang().getTranslation("commands.list.item", world.getName(), players.size(), String.join(", ", players)));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                    Main.getLang().getTranslation("commands.list.itemHover", players.size(), String.join(", ", players), world.getName())
            )));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/worlds tp " + world.getName() + " " + sender.getName()));

            sender.spigot().sendMessage(component);
        }
        sender.sendMessage(UChat.getSeparator(ChatColor.BLUE));
    }

    @Override
    public java.util.List<String> tab(CommandSender sender, String command, String usage, String[] args) {
        return null;
    }
}
