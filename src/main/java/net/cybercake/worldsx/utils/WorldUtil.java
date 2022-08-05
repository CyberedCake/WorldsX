package net.cybercake.worldsx.utils;

import com.google.common.base.Preconditions;
import net.cybercake.cyberapi.common.basic.Time;
import net.cybercake.cyberapi.dependencies.annotations.jetbrains.NotNull;
import net.cybercake.cyberapi.spigot.basic.BetterStackTraces;
import net.cybercake.cyberapi.spigot.chat.Log;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.worldsx.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
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

    @SuppressWarnings({"deprecation"})
    public static void loadWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if(world != null) {
            setLoadValues(worldName, world);
            return;
        }

        try {
            WorldCreator creator = new WorldCreator(worldName.toLowerCase(Locale.ROOT));
            creator.createWorld();
            world = Bukkit.getWorld(worldName);
            if(world == null)
                throw new IllegalStateException("'world' cannot be null!");

            setLoadValues(worldName, world);

            if(Bukkit.getWorlds().contains(Bukkit.getWorld(worldName)))
                Log.info("Successfully loaded new world " + worldName);
            else
                Log.error("An error occurred whilst trying to load the world " + worldName + " [...] world == null");
        } catch (Exception exception) {
            Log.error("An error occurred whilst trying to load the world " + worldName + ": " + exception);
            BetterStackTraces.print(exception);
        }
    }

    public static void setLoadValues(String worldName, World world) {
        setIfNull(worldName, "name", worldName.toLowerCase(Locale.ROOT));
        setIfNull(worldName, "key", world.getKey().toString());
        setIfNull(worldName, "loaded.loaded", true);
        setIfNull(worldName, "loaded.by", WorldUtil.class.getCanonicalName());
        setIfNull(worldName, "loaded.time", Time.getUnix());
        setIfNull(worldName, "type", world.getWorldType().getName());
        setIfNull(worldName, "spawnLocation", world.getSpawnLocation());
    }

    public static void setIfNull(String world, String path, Object toWhat) {
        path = "worlds." + world + "." + path;
        if(Main.getWorlds().values().getObject(path, Object.class) == null) {
            try {
                Main.getWorlds().values().set(path, toWhat);
                Main.getWorlds().save();
            } catch (Exception exception) {
                WorldsXError.raise(Bukkit.getConsoleSender(), exception);
            }
        }
    }

}
