package net.cybercake.worldsx.utils;

import net.cybercake.cyberapi.spigot.basic.BetterStackTraces;
import net.cybercake.cyberapi.spigot.chat.Log;
import net.cybercake.worldsx.Main;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class WorldsXError extends Exception {

    public WorldsXError(String message, Exception cause) {
        super(message, cause);
    }



    public static void raise(CommandSender sender) {
        raise(sender, null, null);
    }

    public static void raise(CommandSender sender, @Nullable Exception cause) {
        raise(sender, (cause == null || cause.toString() == null ? null : cause.toString()), cause);
    }

    public static void raise(CommandSender sender, @Nullable String message, @Nullable Exception cause) {
        WorldsXError exception = (cause == null && (message == null) ? null : new WorldsXError(message, cause));

        Main.getInstance().playSound(sender, Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
        if(exception == null) {
            sender.sendMessage(Main.getLang().getTranslation("commands.general.exception.unknown"));
            Log.error("An unknown error occurred for " + sender.getName() + ", no cause found!");
            return;
        }
        sender.sendMessage(Main.getLang().getTranslation("commands.general.exception.known", exception));
        Log.error("An error occurred for user " + sender.getName() + ": " + exception);
        BetterStackTraces.print(exception);
    }

}
