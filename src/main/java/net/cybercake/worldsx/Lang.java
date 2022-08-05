package net.cybercake.worldsx;

import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.config.Config;
import org.bukkit.configuration.file.FileConfiguration;

public class Lang {

    private final Config config;
    private final FileConfiguration values;

    public Lang() {
        config = Main.getInstance().getConfig("lang");
        config.saveDefaults();
        config.reload();
        values = config.values();
    }

    public String getUncoloredTranslation(String path, Object... replacements) {
        String newMsg = this.values.getString("lang." + path);
        if(newMsg == null)
            return "lang." + path;

        int number = 0;
        for(Object obj : replacements) {
            if(obj == null) obj = "null";
            newMsg = newMsg.replace("{" + number + "}", obj.toString());
            number++;
        }
        return newMsg;
    }

    public String getTranslation(String path, Object... replacements) {
        return UChat.chat(getUncoloredTranslation(path, replacements));
    }

    public Config getConfig() { return this.config; }
    public FileConfiguration getValues() { return this.values; }

}
