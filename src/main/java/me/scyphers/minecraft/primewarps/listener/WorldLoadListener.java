package me.scyphers.minecraft.primewarps.listener;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public record WorldLoadListener(PrimeWarps plugin) implements Listener {

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent event) {
        // TODO - handle loading of warps once world is loaded
    }

}
