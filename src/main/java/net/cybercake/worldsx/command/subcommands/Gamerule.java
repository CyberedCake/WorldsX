package net.cybercake.worldsx.command.subcommands;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.cybercake.cyberapi.common.basic.NumberUtils;
import net.cybercake.worldsx.Main;
import net.cybercake.worldsx.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;

import java.util.Arrays;
import java.util.List;

public class Gamerule extends SubCommand {

    public Gamerule() {
        super("gamerule", "gamerule", Main.getLang().getTranslation("commands.gamerule.description"), "gamerule <gamerule> [value] [world]", "gamerules");
    }


    @Override
    public void perform(CommandSender sender, String command, String usage, String[] args) {
        if(args.length < 1) {
            sender.sendMessage(Main.getLang().getTranslation("commands.general.usage", usage)); return;
        }

        GameRule<?> gameRule = GameRule.getByName(args[0]);
        if(gameRule == null) {
            sender.sendMessage(Main.getLang().getTranslation("commands.general.invalid", "gamerule", args[0])); return;
        }

        if(sender instanceof Player player && args.length == 1) {
            player.sendMessage(Main.getLang().getTranslation("commands.gamerule.current",
                    args[0],
                    (gameRule.getType() == Boolean.class ? getTranslatedBoolean(player.getWorld().getGameRuleValue(gameRule)) : player.getWorld().getGameRuleValue(gameRule)),
                    player.getWorld().getName()
                    )); return;
        }else if(args.length == 1){
            sender.sendMessage(Main.getLang().getTranslation("commands.general.usage", usage)); return;
        }

        if(gameRule.getType() == Boolean.class && !(args[1].equals("true") || args[1].equals("false") || args[1].equalsIgnoreCase(Main.BOOLEAN_TRUE) || args[1].equalsIgnoreCase(Main.BOOLEAN_FALSE))) {
            sender.sendMessage(Main.getLang().getTranslation("commands.gamerule.incorrectDataType", "boolean ('" + Main.BOOLEAN_TRUE + "'/'" + Main.BOOLEAN_FALSE + "')", args[1])); return;
        }else if(gameRule.getType() == Integer.class && !(NumberUtils.isInteger(args[1]))) {
            sender.sendMessage(Main.getLang().getTranslation("commands.gamerule.incorrectDataType", "integer", args[1])); return;
        }

        if(sender instanceof Player player && args.length == 2) {
            modifyGameRule(player.getWorld(), gameRule, args[1]);
            player.sendMessage(Main.getLang().getTranslation("commands.gamerule.set",
                    args[0],
                    args[1],
                    player.getWorld().getName()
                    )); return;
        }else if(args.length == 2) {
            sender.sendMessage(Main.getLang().getTranslation("commands.general.usage", usage)); return;
        }

        World world = Bukkit.getWorld(args[2]);
        if(world == null) {
            sender.sendMessage(Main.getLang().getTranslation("commands.general.invalid", "world", args[2])); return;
        }

        modifyGameRule(world, gameRule, args[1]);
        sender.sendMessage(Main.getLang().getTranslation("commands.gamerule.set",
                args[0],
                args[1],
                args[2]
                ));
    }

    private <T> String getTranslatedBoolean(T gameRule) {
        if(gameRule.toString().equalsIgnoreCase("true"))
            return Main.BOOLEAN_TRUE;
        else if(gameRule.toString().equalsIgnoreCase("false")) {
            return Main.BOOLEAN_FALSE;
        }
        throw new IllegalStateException("Failed to find translated version of " + gameRule.toString() + "!");
    }

    @SuppressWarnings({"all"}) // "Try to generify [...]"
    private <T> void modifyGameRule(World world, GameRule<T> gameRule, String stringValue) {
        // translator
        if(Main.BOOLEAN_FALSE.equalsIgnoreCase(stringValue))
            stringValue = "false";
        else if(Main.BOOLEAN_TRUE.equalsIgnoreCase(stringValue))
            stringValue = "true";

        T value = null;
        if(gameRule.getType() == Integer.class)
            value = (T) Integer.valueOf(stringValue);
        else if(gameRule.getType() == Boolean.class)
            value = (T) Boolean.valueOf(stringValue);
        Preconditions.checkArgument(value != null, "Value cannot be null", value);
        world.setGameRule(gameRule, value);
    }

    @Override
    public List<String> tab(CommandSender sender, String command, String usage, String[] args) {
        if(args.length == 1)
            return Arrays.stream(GameRule.values())
                    .map(GameRule::getName)
                    .toList();
        if(args.length >= 2) {
            GameRule<?> gameRule = GameRule.getByName(args[0]);
            if(gameRule == null)
                return null;

            if(args.length == 2) {
                if(gameRule.getType() == Boolean.class)
                    return ImmutableList.of(Main.BOOLEAN_TRUE, Main.BOOLEAN_FALSE);
                else if(gameRule.getType() == Integer.class) {
                    int defaultValue = getDefaultValue(gameRule);
                    return (defaultValue == -1 ? null : List.of(String.valueOf(defaultValue)));
                }
            }
            if(args.length == 3) {
                return Bukkit.getWorlds()
                        .stream()
                        .map(WorldInfo::getName)
                        .toList();
            }
        }
        return null;
    }

    private int getDefaultValue(GameRule<?> gameRule){
        if(gameRule.getType() != Integer.class)
            throw new IllegalArgumentException(GameRule.class.getCanonicalName() + " must be of an " + Integer.class.getCanonicalName() + " class, found " + gameRule.getType().getCanonicalName() + "!");

        return switch(gameRule.getName()) { // if no default value is found here, it will default to nothing (just not tab completing)
            case "maxCommandChainLength" -> 65536;
            case "maxEntityCramming" -> 24;
            case "playersSleepingPercentage" -> 100;
            case "randomTickSpeed" -> 3;
            case "spawnRadius" -> 10;
            default -> -1;
        };
    }
}
