package me.Scyy.PrimeWarps.Event;

import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.HashSet;
import java.util.Set;

public class WorldLoadListener implements Listener {

    private final Set<String> requiredWorlds;

    private final Plugin plugin;

    public WorldLoadListener(Plugin plugin) {
        this.plugin = plugin;
        this.requiredWorlds = new HashSet<>(plugin.getCFH().getSettings().getWorlds());
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        // Check if all specified worlds are loaded and if so, load all warps
        if (allWorldsLoaded()) this.plugin.loadWarps();
    }

    public boolean allWorldsLoaded() {
        for (String requiredWorld : requiredWorlds) {
            if (Bukkit.getWorld(requiredWorld) == null) return false;
        }
        return true;
    }

}
