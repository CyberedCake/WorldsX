package net.cybercake.worldsx.command.subcommands;

import net.cybercake.worldsx.Main;
import net.cybercake.worldsx.command.SubCommand;
import net.cybercake.worldsx.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;

import java.util.List;

public class Teleport extends SubCommand {

    public Teleport() {
        super("tp", "teleport", Main.getLang().getTranslation("commands.teleport.description"), "tp <world> [player]", "teleport", "spawn");
    }

    @Override
    public void perform(CommandSender sender, String command, String usage, String[] args) {
        if(args.length == 0) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(Main.getLang().getTranslation("commands.general.usage", usage)); return;
            }
            teleportPlayer(player, player, player.getWorld());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F); return;
        }
        World world = Bukkit.getWorld(args[0]);
        if(world == null) {
            sender.sendMessage(Main.getLang().getTranslation("commands.general.invalid", "world", args[0])); return;
        }

        if(args.length == 1) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(Main.getLang().getTranslation("commands.general.usage", usage)); return;
            }
            teleportPlayer(player, player, world);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F); return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if(target == null) {
            sender.sendMessage(Main.getLang().getTranslation("commands.general.invalid", "player", args[1])); return;
        }

        teleportPlayer(sender, target, world);
        if(sender == target)
            Main.getInstance().playSound(sender, Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
    }

    public void teleportPlayer(CommandSender msgTo, Player teleportWho, World worldSpawn) {
        teleportWho.teleport(WorldUtil.getWorldSpawn(worldSpawn));
        msgTo.sendMessage(Main.getLang().getTranslation("commands.teleport.success", (msgTo == teleportWho ? "yourself" : teleportWho.getName()), worldSpawn.getName()));
    }

    @Override
    public List<String> tab(CommandSender sender, String command, String usage, String[] args) {
        if(args.length == 1)
            return Bukkit.getWorlds()
                    .stream()
                    .map(WorldInfo::getName)
                    .toList();
        if(args.length == 2)
            return Main.getInstance().getOnlinePlayersUsernames();
        return null;
    }
}
