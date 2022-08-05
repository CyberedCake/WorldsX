package net.cybercake.worldsx.command.subcommands;

import net.cybercake.worldsx.Main;
import net.cybercake.worldsx.command.SubCommand;
import net.cybercake.worldsx.utils.WorldUtil;
import net.cybercake.worldsx.utils.WorldsXError;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.WorldInfo;

import java.util.List;

public class Unload extends SubCommand {

    public Unload() {
        super("unload", "unload", Main.getLang().getTranslation("commands.unload.description"), "unload <world>", "disable");
    }

    @Override
    public void perform(CommandSender sender, String command, String usage, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(Main.getLang().getTranslation("commands.general.usage", usage)); return;
        }
        World world = Bukkit.getWorld(args[0]);
        if(world == null || !Bukkit.getWorlds().contains(world)) {
            sender.sendMessage(Main.getLang().getTranslation("commands.general.invalid", "world", args[1])); return;
        }
        if(Main.getInstance().getMainWorld().equals(world)) {
            sender.sendMessage(Main.getLang().getTranslation("commands.unload.mainWorld")); return;
        }
        Main.getInstance().getOnlinePlayers()
                .stream()
                .filter(player -> player.getWorld().equals(world))
                .forEach(player -> player.teleport(WorldUtil.getWorldSpawn(Main.getInstance().getMainWorld())));
        try {
            Main.getWorlds().values().set("worlds." + args[0] + ".loaded", false);
            Main.getWorlds().save();

            Bukkit.unloadWorld(args[0], true);
            sender.sendMessage(Main.getLang().getTranslation("commands.unload.success", args[0]));

            Main.getInstance().playSound(sender, Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
        } catch (Exception exception) {
            WorldsXError.raise(sender, "An error occurred whilst unloading " + args[0] + " (user=" + sender.getName() + ")", exception);
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String command, String usage, String[] args) {
        if(args.length == 1)
            return Bukkit.getWorlds()
                    .stream()
                    .map(WorldInfo::getName)
                    .toList();
        return null;
    }
}
