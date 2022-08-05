package net.cybercake.worldsx.command;

import net.cybercake.cyberapi.dependencies.reflections.Reflections;
import net.cybercake.cyberapi.spigot.chat.Log;
import net.cybercake.worldsx.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static net.cybercake.worldsx.command.CommandManager.SUB_COMMANDS_PACKAGE;

public abstract class SubCommand {

    private static final List<SubCommand> subCommands = new ArrayList<>();

    public static void registerAll() {
        for(Class<? extends SubCommand> clazz : new Reflections(SUB_COMMANDS_PACKAGE).getSubTypesOf(SubCommand.class)) {
            try {
                SubCommand newInstance = clazz.getDeclaredConstructor().newInstance();
                subCommands.add(newInstance);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException noSuchMethodException) {
                Log.error("An error occurred whilst registering sub-commands for " + Main.getInstance().getDescription().getName() + " (version " + Main.getInstance().getDescription().getVersion() + "): " + ChatColor.DARK_GRAY + noSuchMethodException);
            }
        }
    }

    public static List<SubCommand> getSubCommands() { return subCommands; }
    public static List<SubCommand> getSubCommandsFor(CommandSender sender) {
        if(sender.hasPermission(CommandManager.PERMISSION_ROOT + "*")) return subCommands;
        return getSubCommands()
                .stream()
                .filter(subCommand -> (subCommand.getPermission() == null || sender.hasPermission(CommandManager.PERMISSION_ROOT + subCommand.getPermission())))
                .toList();
    }
    public static @Nullable SubCommand getSubCommandClass(Class<? extends SubCommand> clazz) {
        return getSubCommands()
                .stream()
                .filter(subCommand -> subCommand.getClass().getCanonicalName().equalsIgnoreCase(clazz.getCanonicalName()))
                .findFirst()
                .orElse(null);
    }
    public static @Nullable SubCommand getSubCommandNameOrAlias(String nameOrAlias, boolean ignoreCase) {
        return getSubCommands()
                .stream()
                .filter(subCommand -> {
                    if(ignoreCase)
                        return subCommand.getName().equalsIgnoreCase(nameOrAlias) || Arrays.stream(subCommand.getAliases()).map(String::toLowerCase).toList().contains(nameOrAlias.toLowerCase(Locale.ROOT));
                    else
                        return subCommand.getName().equals(nameOrAlias) || Arrays.stream(subCommand.getAliases()).toList().contains(nameOrAlias);
                })
                .findFirst()
                .orElse(null);
    }


    private final String name;
    private final @Nullable String permission;
    private final String description;
    private final String usage;
    private final String[] aliases;

    public SubCommand(String name, @Nullable String permission, String description, String usage, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
    }

    public String getName() { return this.name; }
    public @Nullable String getPermission() { return this.permission; }
    public String getDescription() { return this.description; }
    public String getUsage() { return this.usage; }
    public String[] getAliases() { return this.aliases; }

    public abstract void perform(CommandSender sender, String command, String usage, String[] args);
    public abstract List<String> tab(CommandSender sender, String command, String usage, String[] args);

}
