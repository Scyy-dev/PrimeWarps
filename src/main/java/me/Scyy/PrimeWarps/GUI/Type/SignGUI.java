package me.Scyy.PrimeWarps.GUI.Type;

import me.Scyy.PrimeWarps.GUI.sign.SignManager;
import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SignGUI implements GUI<SignChangeEvent> {

    protected final GUI<?> lastGUI;

    protected final Plugin plugin;

    protected final Player player;

    private final SignManager signManager;

    private final int signID;

    public SignGUI(GUI<?> lastGUI, Plugin plugin, Player player, String[] text) {
        this.lastGUI = lastGUI;
        this.plugin = plugin;
        this.player = player;
        this.signManager = plugin.getSignManager();

        signID = signManager.initSign(this, text);
    }

    /**
     * The outcome of this method should never be another SignGUI
     * @param event the event to handle the GUI interaction
     * @return the new GUI
     */
    @Override
    public abstract @NotNull GUI<?> handleInteraction(SignChangeEvent event);

    @Override
    public void open(Player player) {
        if (!signManager.isValidSign(signID)) {
            plugin.getCFH().getPlayerMessenger().msg(player, "errorMessages.signNotAvailable");
            lastGUI.open(player);
        } else {
            Sign sign = signManager.getSign(signID);
            player.openSign(sign);
        }
    }

    @Override
    public @Nullable GUI<?> getLastGUI() {
        return lastGUI;
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public boolean shouldClose() {
        return false;
    }

}
