package net.cybercake.worldsx;

import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import net.cybercake.cyberapi.common.builders.settings.Settings;
import net.cybercake.cyberapi.spigot.CyberAPI;
import net.cybercake.cyberapi.spigot.chat.Log;
import net.cybercake.cyberapi.spigot.config.Config;
import net.cybercake.cyberapi.spigot.server.commands.CommandManager;
import net.cybercake.worldsx.command.CommandListener;
import net.cybercake.worldsx.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;

import java.util.ArrayList;
import java.util.List;

public class Main extends CyberAPI {

    public static String BOOLEAN_TRUE = "true";
    public static String BOOLEAN_FALSE = "false";

    private static Lang lang;
    public static Lang getLang() { return lang; }
    public static void setLang(Lang lang) { Main.lang = lang; }

    private static Config worlds;
    public static Config getWorlds() { return worlds; }

    @Override
    public void onEnable() {
        long mss = System.currentTimeMillis();
        startCyberAPI(Settings.builder()
                .name("WorldsX")
                .prefix("WorldsX")
                .showPrefixInLogs(true)
                .muteStartMessage(true)
                .checkForUpdates(false)
                .commandsPath("net.cybercake.worldsx.command")
                .build());

        saveDefaultConfig();
        reloadConfig();

        worlds = new Config("worlds");
        worlds.saveDefaults();
        worlds.reload();

        lang = new Lang();
        BOOLEAN_TRUE = getLang().getTranslation("commands.general.boolean.true");
        BOOLEAN_FALSE = getLang().getTranslation("commands.general.boolean.false");

        PluginCommand command = getCommand("worldsx");
        if(CommodoreProvider.isSupported()) {
            Commodore commodore = CommodoreProvider.getCommodore(this);
            commodore.register(command, net.cybercake.worldsx.command.Commodore.forCommand());
        }

        registerListener(new CommandListener());

        List<String> worlds = new ArrayList<>(WorldUtil.getAllUnloadedWorlds());
        worlds.addAll(Main.getWorlds().values().getConfigurationSection("worlds").getKeys(true));
        for(String world : worlds) {
            if(!getWorlds().values().getBoolean("worlds." + world + ".loaded.loaded")) continue;
            WorldUtil.loadWorld(world);
        }
        for(World world : Bukkit.getWorlds()) {
            if(getWorlds().values().getConfigurationSection("worlds." + world) == null) WorldUtil.loadWorld(world.getName());
            if(getWorlds().values().getBoolean("worlds." + world + ".loaded.loaded")) continue;
            Bukkit.unloadWorld(world, true);
        }

        Log.info(lang.getTranslation("general.onEnable", this.getDescription().getName() + " [v" + this.getDescription().getVersion() + "]", (System.currentTimeMillis()-mss)));
    }

    @Override
    public void onDisable() {
        long mss = System.currentTimeMillis();

        Log.info(lang.getTranslation("general.onDisable", this.getDescription().getName() + " [v" + this.getDescription().getVersion() + "]", (System.currentTimeMillis()-mss)));
    }
}
