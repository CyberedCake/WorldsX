package net.cybercake.worldsx.command.subcommands;

import net.cybercake.cyberapi.common.basic.Time;
import net.cybercake.worldsx.Main;
import net.cybercake.worldsx.command.SubCommand;
import net.cybercake.worldsx.utils.WorldUtil;
import net.cybercake.worldsx.utils.WorldsXError;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Load extends SubCommand {

    public Load() {
        super("load", "load", Main.getLang().getTranslation("commands.load.description"), "load <worldName> <worldType>", "create");
    }

    @Override
    public void perform(CommandSender sender, String command, String usage, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(Main.getLang().getTranslation("commands.general.usage", usage)); return;
        }

        try {
            WorldType type = null;
            if(args.length == 2) {
                type = Arrays.stream(WorldType.values())
                        .filter(typeCheck -> args[1].equalsIgnoreCase(typeCheck.getName()))
                        .findFirst()
                        .orElse(null);
            }

            if(type == null){
                sender.sendMessage(Main.getLang().getTranslation("commands.general.foundValid", "world type", String.join(", ", Arrays.stream(WorldType.values()).map(WorldType::getName).toList()))); return;
            }

            World world = Bukkit.getWorld(args[0]);
            if(world != null) {
                sender.sendMessage(Main.getLang().getTranslation("commands.load.alreadyLoaded")); return;
            }else{
                File file = new File(Bukkit.getWorldContainer(), args[0]);
                if(file.exists() && !(WorldUtil.isWorldFolder(file))) {
                    sender.sendMessage(Main.getLang().getTranslation("commands.load.fileNotAWorld")); return;
                }
            }

            sender.sendMessage(Main.getLang().getTranslation("commands.load.creationInProgress"));

            WorldCreator creator = new WorldCreator(args[0].toLowerCase(Locale.ROOT));
            creator.type(type);
            creator.environment(World.Environment.NORMAL);
            creator.createWorld();

            World worldCheck = Bukkit.getWorld(args[0]);
            if(worldCheck != null) {
                sender.sendMessage(Main.getLang().getTranslation("commands.load.success", args[0]));

                WorldUtil.setIfNull(args[0], "name", args[0].toLowerCase(Locale.ROOT));
                WorldUtil.setIfNull(args[0], "key", worldCheck.getKey().toString());
                WorldUtil.setIfNull(args[0], "loaded.loaded", true);
                WorldUtil.setIfNull(args[0], "loaded.by", sender.getName());
                WorldUtil.setIfNull(args[0], "loaded.time", Time.getUnix());
                WorldUtil.setIfNull(args[0], "type", type.getName().toLowerCase(Locale.ROOT));
                WorldUtil.setIfNull(args[0], "spawnLocation", worldCheck.getSpawnLocation());

                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    if(sender instanceof Player player)
                        player.teleport(WorldUtil.getWorldSpawn(worldCheck));

                    Main.getInstance().playSound(sender, Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                }, 20L);
            }else if(worldCheck == null) {
                sender.sendMessage(Main.getLang().getTranslation("commands.load.failed", args[0]));

                Main.getInstance().playSound(sender, Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
            }
        } catch (Exception exception) {
            WorldsXError.raise(sender, "An error occurred during the world creation/loading process for " + args[0] + " (user=" + sender.getName() + ")", exception);
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String command, String usage, String[] args) {
        if(args.length == 1) return WorldUtil.getAllUnloadedWorlds();
        if(args.length == 2) return Arrays.stream(WorldType.values()).map(WorldType::getName).map(String::toLowerCase).toList();
        return null;
    }
}
