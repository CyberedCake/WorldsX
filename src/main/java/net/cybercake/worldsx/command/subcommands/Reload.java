package net.cybercake.worldsx.command.subcommands;

import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import net.cybercake.cyberapi.spigot.chat.Log;
import net.cybercake.cyberapi.spigot.server.commands.CommandManager;
import net.cybercake.worldsx.Lang;
import net.cybercake.worldsx.Main;
import net.cybercake.worldsx.command.SubCommand;
import net.cybercake.worldsx.utils.WorldsXError;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;

public class Reload extends SubCommand {

    public Reload(){
        super("reload", "reload", Main.getLang().getTranslation("commands.reload.description"), "reload", "rl");
    }

    @Override
    public void perform(CommandSender sender, String command, String usage, String[] args) {
        long mss = System.currentTimeMillis();

        Exception exception = null;
        String exceptionFile = "";

        try {
            if(!getFileInDataFolder("config.yml").exists()) Main.getInstance().getMainConfig().saveDefaults();
            Main.getInstance().getMainConfig().reload();
        } catch (Exception ex) { exception = ex; exceptionFile = "config.yml"; }
        try {
            Main.setLang(new Lang());
            if(!getFileInDataFolder("lang.yml").exists()) Main.getLang().getConfig().saveDefaults();
            Main.getLang().getConfig().reload();
            Main.BOOLEAN_TRUE = Main.getLang().getTranslation("commands.general.boolean.true");
            Main.BOOLEAN_FALSE = Main.getLang().getTranslation("commands.general.boolean.false");

            PluginCommand pluginCommand = Main.getInstance().getCommand("worldsx");
            if(CommodoreProvider.isSupported() && pluginCommand != null) {
                Commodore commodore = CommodoreProvider.getCommodore(Main.getInstance());
                commodore.register(pluginCommand, net.cybercake.worldsx.command.Commodore.forCommand());
            }
        } catch (Exception ex) { exception = ex; exceptionFile = "lang.yml"; }

        if(exception != null) {
            WorldsXError.raise(sender, "An error occurred whilst trying to reload " + exceptionFile + "! (user=" + sender.getName() + ")", exception);
            Main.getInstance().playSound(sender, Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
        } else {
            NumberFormat format = NumberFormat.getInstance();
            format.setGroupingUsed(true);
            sender.sendMessage(Main.getLang().getTranslation("commands.reload.success", format.format(System.currentTimeMillis()-mss)));
            Main.getInstance().playSound(sender, Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
        }
    }

    private File getFileInDataFolder(String file) {
        return new File(Main.getInstance().getDataFolder(), file);
    }

    @Override
    public List<String> tab(CommandSender sender, String command, String usage, String[] args) {
        return null;
    }
}
