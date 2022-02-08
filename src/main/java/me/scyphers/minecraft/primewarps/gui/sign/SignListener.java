package me.scyphers.minecraft.primewarps.gui.sign;

import me.scyphers.scycore.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

public record SignListener(SignManager manager) implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChangeEvent(SignChangeEvent event) {
        BlockState state = event.getBlock().getState();
        if (state instanceof Sign sign) {
            int signID = manager.getSignTag(sign);
            if (signID == -1) return;
            SignGUI oldGUI = manager.getGUI(signID);

            // handle the interaction
            GUI<?> newGUI = oldGUI.handleInteraction(event);

            // Remove the sign
            manager.removeSign(signID);

            // Check if the Sign should be closed
            if (oldGUI.shouldClose()) {
                Bukkit.getScheduler().runTask(oldGUI.getPlugin(), () -> oldGUI.getPlayer().closeInventory());
            } else {
                if (newGUI instanceof SignGUI)
                    throw new IllegalStateException("Cannot open a Sign GUI from a Sign GUI");
                Bukkit.getScheduler().runTask(oldGUI.getPlugin(), () -> newGUI.open(oldGUI.getPlayer()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignBreakEvent(BlockBreakEvent event) {
        BlockState blockState = event.getBlock().getState();
        if (blockState instanceof Sign sign) {
            int signID = manager.getSignTag(sign);
            // Cancel the event if the signID is valid
            event.setCancelled(signID != -1);
        }

    }

}
