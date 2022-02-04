package me.scyphers.minecraft.primewarps.gui;

import me.scyphers.scycore.BasePlugin;
import me.scyphers.scycore.gui.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreateWarpGUI implements GUI<SignChangeEvent> {

    // TODO - handle sign GUIs

    @Override
    public @NotNull GUI<?> handleInteraction(SignChangeEvent signChangeEvent) {
        return null;
    }

    @Override
    public void open(Player player) {

    }

    @Override
    public @Nullable GUI<?> getLastGUI() {
        return null;
    }

    @Override
    public @NotNull BasePlugin getPlugin() {
        return null;
    }

    @Override
    public @NotNull Player getPlayer() {
        return null;
    }

    @Override
    public boolean shouldClose() {
        return false;
    }
}
