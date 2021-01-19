package me.Scyy.PrimeWarps.GUI;

import me.Scyy.PrimeWarps.GUI.Type.GUI;
import me.Scyy.PrimeWarps.GUI.Type.InventoryGUI;
import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class UninteractableGUI extends InventoryGUI {

    public UninteractableGUI(InventoryGUI lastGUI) {
        super(lastGUI, lastGUI.getPlugin(), lastGUI.getPlayer(), lastGUI.getName(), lastGUI.getSize());
        this.inventoryItems = lastGUI.getInventoryItems();
    }

    @Override
    public @NotNull GUI<?> handleInteraction(InventoryClickEvent event) {
        event.setCancelled(true);
        return this;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }
}
