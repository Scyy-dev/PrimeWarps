package me.scyphers.minecraft.primewarps.external;

import me.scyphers.minecraft.primewarps.warps.Warp;
import net.geekxboy.skyblock.events.IslandDeleteEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record SkyblockListener(SkyblockManager manager) implements Listener {

    @EventHandler
    public void onIslandDeleteEvent(IslandDeleteEvent event) {
        if (!manager().isPluginLoaded()) return;

        UUID islandUUID = event.getIsland().getUuid();

        manager.getPlugin().getLogger().info("Deleting all warps for island " + islandUUID);

        List<Warp> islandWarps = new ArrayList<>(manager.getPlugin().getWarps().getIslandWarps(islandUUID));
        islandWarps.forEach(warp -> manager.getPlugin().getWarps().removeWarp(warp.getName()));

    }

}
