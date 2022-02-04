package me.scyphers.minecraft.primewarps.gui;

import me.scyphers.scycore.BasePlugin;
import me.scyphers.scycore.gui.GUI;
import me.scyphers.scycore.gui.InventoryGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerWarpListGUI extends InventoryGUI {

    public PlayerWarpListGUI(@Nullable GUI<?> lastGUI, @NotNull BasePlugin plugin, @NotNull Player player, @NotNull String name, int size) {
        super(lastGUI, plugin, player, name, size);
    }

    @Override
    public @NotNull GUI<?> handleInteraction(InventoryClickEvent inventoryClickEvent) {
        return null;
    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }
}
