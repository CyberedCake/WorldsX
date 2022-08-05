package net.cybercake.worldsx;

import net.cybercake.cyberapi.common.builders.settings.Settings;
import net.cybercake.cyberapi.spigot.CyberAPI;
import net.cybercake.cyberapi.spigot.chat.Log;
import net.cybercake.cyberapi.spigot.config.Config;
import net.cybercake.worldsx.command.CommandListener;

public class Main extends CyberAPI {

    public static String BOOLEAN_TRUE = "true";
    public static String BOOLEAN_FALSE = "false";

    private static Lang lang;
    public static Lang getLang() { return lang; }

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

        registerListener(new CommandListener());

        Log.info(lang.getTranslation("general.onEnable", this.getDescription().getName() + " [v" + this.getDescription().getVersion() + "]", (System.currentTimeMillis()-mss)));
    }

    @Override
    public void onDisable() {
        long mss = System.currentTimeMillis();

        Log.info(lang.getTranslation("general.onDisable", this.getDescription().getName() + " [v" + this.getDescription().getVersion() + "]", (System.currentTimeMillis()-mss)));
    }
}
