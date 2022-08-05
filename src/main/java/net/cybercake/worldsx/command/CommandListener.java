package net.cybercake.worldsx.command;

import net.cybercake.worldsx.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CommandListener implements Listener {

    @EventHandler
    public void onPlayerCommandSendEvent(PlayerCommandSendEvent event) {
        List<SubCommand> subCommandsForUser = SubCommand.getSubCommandsFor(event.getPlayer());

        String namespace = Main.getInstance().getDescription().getName().toLowerCase(Locale.ROOT);
        event.getCommands().remove(namespace + ":" + CommandManager.INSTANCE.getMainCommand().getName());
        event.getCommands().removeAll(
                Arrays.stream(CommandManager.INSTANCE.getMainCommand().getAliases())
                        .map(alias -> namespace + ":" + alias)
                        .toList()
        );

        if(!event.getPlayer().hasPermission(CommandManager.PERMISSION_ROOT + "*") && subCommandsForUser.size() < 2) {
            event.getCommands().remove(CommandManager.INSTANCE.getMainCommand().getName());
            event.getCommands().removeAll(
                    Arrays.asList(CommandManager.INSTANCE.getMainCommand().getAliases())
            );
        }
    }

}
