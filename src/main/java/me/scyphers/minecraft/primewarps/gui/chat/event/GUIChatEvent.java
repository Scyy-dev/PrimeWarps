package me.scyphers.minecraft.primewarps.gui.chat.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class GUIChatEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final Player player;

    private final String rawText;

    public GUIChatEvent(Player player, String rawText) {
        this.player = player;
        this.rawText = rawText;
    }

    public Player getPlayer() {
        return player;
    }

    public String getRawText() {
        return rawText;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }


}
