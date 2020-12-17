package me.Scyy.PrimeWarps.Event;

import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.HashSet;
import java.util.Set;

public class WorldLoadListener implements Listener {

    private final Set<String> loadedWorlds;

    private final Set<String> requiredWorlds;

    private final Plugin plugin;

    public WorldLoadListener(Plugin plugin) {
        this.plugin = plugin;
        this.loadedWorlds = new HashSet<>();
        this.requiredWorlds = new HashSet<>(plugin.getCFH().getSettings().getWorlds());
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        this.loadedWorlds.add(event.getWorld().getName());

        // Check if all specified worlds are loaded and if so, load all warps
        if (allWorldsLoaded()) this.plugin.loadWarps();

    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        this.loadedWorlds.remove(event.getWorld().getName());
    }

    public boolean allWorldsLoaded() {
        for (String loadedWorld : loadedWorlds) {
            if (!requiredWorlds.contains(loadedWorld)) return false;
        }
        return true;
    }

}
