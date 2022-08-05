package net.cybercake.worldsx.command.subcommands;

import net.cybercake.cyberapi.common.basic.NumberUtils;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.worldsx.Main;
import net.cybercake.worldsx.command.SubCommand;
import net.cybercake.worldsx.utils.WorldsXError;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SetSpawn extends SubCommand {

    public SetSpawn() {
        super("setspawn", "setspawn", Main.getLang().getTranslation("commands.setspawn.description"), "setspawn [<x> <y> <z> <yaw> <pitch> [world]]");
    }

    @Override
    public void perform(CommandSender sender, String command, String usage, String[] args) {
        if(args.length == 0) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(Main.getLang().getTranslation("commands.general.usage", usage)); return;
            }
            try {
                Location location = player.getLocation();

                Main.getWorlds().values().set("worlds." + player.getWorld().getName() + ".spawnLocation", location);
                Main.getWorlds().save();

                player.getWorld().setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw());

                player.sendMessage(Main.getLang().getTranslation("commands.setspawn.success.yourLocation"));
                player.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 2F);
            } catch (Exception exception) {
                WorldsXError.raise(sender, exception);
            }
        }else if(args.length > 0) {
            try {
                for(String arg : Arrays.copyOfRange(args, 1, (args.length < 6 ? args.length : 5))) {
                    if(!NumberUtils.isDouble(arg)) {
                        sender.sendMessage(Main.getLang().getTranslation("commands.general.invalid", "decimal", arg)); return;
                    }
                }
                if (args.length < (sender instanceof Player ? 5 : 6)) {
                    sender.sendMessage(Main.getLang().getTranslation("commands.setspawn.failed.parsing", usage)); return;
                }

                World world;
                if(sender instanceof Player player) {
                    if(args.length >= 6) {
                        world = Bukkit.getWorld(args[5]);
                        if(world == null) {
                            sender.sendMessage(Main.getLang().getTranslation("commands.general.invalid", "world", args[5]));
                            return;
                        }
                    }else
                        world = player.getWorld();
                }else{
                    world = Bukkit.getWorld(args[5]);
                    if(world == null) {
                        sender.sendMessage(Main.getLang().getTranslation("commands.general.invalid", "world", args[5])); return;
                    }
                }

                Location location = new Location(world, Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Float.parseFloat(args[3]), Float.parseFloat(args[4]));

                Main.getWorlds().values().set("worlds." + location.getWorld().getName() + ".spawnLocation", location);
                Main.getWorlds().save();
                sender.sendMessage(Main.getLang().getTranslation("commands.setspawn.success.coordinates",
                        location.getWorld().getName(),
                        args[0],
                        args[1],
                        args[2],
                        args[3],
                        args[4]
                ));
                Main.getInstance().playSound(sender, Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 2F);
            } catch (Exception exception) {
                WorldsXError.raise(sender, exception);
                // WorldsXError.raise(sender, "An error occurred during settings the spawn for " + (sender instanceof Player player ? player.getWorld().getName() : (args.length > 6 ? args[5] : "(unknown world)")) + " (user=" + sender.getName() + ")", exception);
            }
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String command, String usage, String[] args) {
        if(args.length == 6) return Bukkit.getWorlds()
                .stream()
                .map(WorldInfo::getName)
                .toList();
        if(args.length < 6 && sender instanceof Player player) {
            Location location = player.getLocation();
            return tabAlt(args, Math.round(location.getX()) + " " + Math.round(location.getY()) + " " + Math.round(location.getZ()) + " " +Math.round(location.getYaw()) + " " + Math.round(location.getPitch()));
        }
        return null;
    }

    private List<String> tabAlt(String[] args, String toComplete) {
        String[] xyz = Arrays.copyOfRange(toComplete.split(" "), 0, 3);
        String[] yawPitch = Arrays.copyOfRange(toComplete.split(" "), 3, toComplete.split(" ").length);

        return Collections.singletonList(String.join(" ",
                args.length < 4
                ? Arrays.copyOfRange(xyz, args.length-1, xyz.length)
                : Arrays.copyOfRange(yawPitch, Arrays.copyOfRange(args, 3, args.length).length-1, yawPitch.length)
        ));
    }
}
