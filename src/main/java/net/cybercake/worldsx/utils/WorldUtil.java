package net.cybercake.worldsx.utils;

import com.google.common.base.Preconditions;
import net.cybercake.cyberapi.dependencies.annotations.jetbrains.NotNull;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.worldsx.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class WorldUtil {

    @SuppressWarnings({"all"})
    public static void deleteWorld(File path) {
        if(path.exists()) {
            File[] files = path.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteWorld(file);
                } else {
                    file.delete();
                }
            }
        }
        path.delete();
    }

    public static List<String> getAllUnloadedWorlds() {
        List<String> worlds = new ArrayList<>();

        File[] worldContainer = Bukkit.getServer().getWorldContainer().listFiles();
        if(worldContainer == null)
            throw new IllegalStateException("Found zero worlds in WorldContainer, there must at least be one! " + Bukkit.getServer().getWorldContainer().getAbsolutePath());
        Arrays.stream(worldContainer)
                .filter(File::isDirectory)
                .filter(file -> (Arrays.stream(Objects.requireNonNull(file.list()))
                        .filter(file1 -> file1.endsWith(".dat"))
                        .findFirst()
                        .orElse(null) != null)
                )
                .filter(file -> Bukkit.getWorld(file.getName()) == null)
                .forEach(file -> worlds.add(file.getName()));

        return worlds;
    }

    public static Location getWorldSpawn(@NotNull World world) {
        Location spawnLocation = Main.getWorlds().values().getLocation("worlds." + world.getName() + ".spawnLocation");
        if(spawnLocation == null) {
            Main.getWorlds().values().set("worlds." + world.getName() + ".spawnLocation", world.getSpawnLocation());
            return world.getSpawnLocation();
        }
        return spawnLocation;
    }

    public static Location getWorldSpawn(@NotNull String worldName) {
        World world = Bukkit.getWorld(worldName);
        Preconditions.checkArgument(world != null, "World " + worldName + " was not found, please enter a valid world!", worldName);
        return getWorldSpawn(world);
    }

    public static boolean isWorldFolder(File worldFolder){
        File[] files = worldFolder.listFiles((file, name) -> name.toLowerCase(Locale.ROOT).endsWith(".dat"));
        return files != null && files.length > 0;
    }

}
