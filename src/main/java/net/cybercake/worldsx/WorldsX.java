package net.cybercake.worldsx;

import net.cybercake.cyberapi.common.builders.settings.Settings;
import net.cybercake.cyberapi.spigot.CyberAPI;
import net.cybercake.cyberapi.spigot.chat.Log;

public class WorldsX extends CyberAPI {

    @Override
    public void onEnable() {
        long mss = System.currentTimeMillis();
        startCyberAPI(Settings.builder()
                .name("WorldsX")
                .prefix("WorldsX")
                .showPrefixInLogs(true)
                .muteStartMessage(true)
                .checkForUpdates(false)
                .build());

        Log.info("Loaded " + this.getDescription().getName() + " [v" + this.getDescription().getVersion() + "] in " + (System.currentTimeMillis()-mss) + "ms!");
    }

    @Override
    public void onDisable() {
        long mss = System.currentTimeMillis();

        Log.info("Unloaded " + this.getDescription().getName() + " [v" + this.getDescription().getVersion() + "] in " + (System.currentTimeMillis()-mss) + "ms!");
    }
}
