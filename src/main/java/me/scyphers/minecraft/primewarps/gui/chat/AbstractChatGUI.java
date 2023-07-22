package me.scyphers.minecraft.primewarps.gui.chat;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.gui.chat.event.GUIChatEvent;
import me.scyphers.scycore.gui.GUI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractChatGUI<T extends GUIChatEvent> implements GUI<T> {

    private final GUI<?> lastGUI;
    private final PrimeWarps plugin;
    private final Player player;

    public AbstractChatGUI(GUI<?> lastGUI, PrimeWarps plugin, Player player) {
        this.lastGUI = lastGUI;
        this.plugin = plugin;
        this.player = player;
        this.registerChat();
    }

    public abstract void registerChat();

    @Override
    public @Nullable GUI<?> getLastGUI() {
        return lastGUI;
    }

    @Override
    public @NotNull PrimeWarps getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

}
