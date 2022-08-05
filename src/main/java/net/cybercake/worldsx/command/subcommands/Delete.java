package net.cybercake.worldsx.command.subcommands;

import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.worldsx.Main;
import net.cybercake.worldsx.command.SubCommand;
import net.cybercake.worldsx.utils.WorldUtil;
import net.cybercake.worldsx.utils.WorldsXError;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.WorldInfo;

import javax.annotation.Nullable;
import java.util.List;

public class Delete extends SubCommand {

    public Delete() {
        super("delete", "delete", Main.getLang().getTranslation("commands.delete.description"), "delete <world>", "deleteworld", "remove");
    }

    @Override
    public void perform(CommandSender sender, String command, String usage, String[] args) {
        if(args.length < 1) {
            sender.sendMessage(Main.getLang().getTranslation("commands.general.usage", usage));
            return;
        }

        try {
            @Nullable World world = Bukkit.getWorld(args[0]);
            if (world == null) {
                sender.sendMessage(Main.getLang().getTranslation("commands.general.invalid", "world", args[0])); return;
            }
            if(Main.getInstance().getMainWorld().equals(world)) {
                sender.sendMessage(Main.getLang().getTranslation("commands.delete.mainWorld")); return;
            }

            sender.sendMessage(Main.getLang().getTranslation("commands.delete.deletionInProgress"));

//            Main.getInstance().getOnlinePlayers()
//                    .stream()
//                    .filter(player -> player.getWorld().equals(world))
//                    .forEach(player -> player.teleport(SetSpawn.getWorldSpawn(Main.getInstance().getMainWorld())));

            WorldUtil.deleteWorld(world.getWorldFolder());
            Bukkit.unloadWorld(world, false);
            @Nullable World worldCheck = Bukkit.getWorld(args[0]);

            if(worldCheck == null) {
                sender.sendMessage(Main.getLang().getTranslation("commands.delete.success", args[0]));

                Main.getWorlds().values().set("worlds." + args[0], null);
                Main.getWorlds().save();

                Main.getInstance().playSound(sender, Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
            }else if(worldCheck != null) {
                sender.sendMessage(Main.getLang().getTranslation("commands.delete.failed", args[0]));

                Main.getInstance().playSound(sender, Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
            }
        } catch (Exception exception) {
            WorldsXError.raise(sender, "An error occurred during the world deletion process for " + args[0] + " (user=" + sender.getName() + ")", exception);
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
