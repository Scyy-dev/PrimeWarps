package me.Scyy.PrimeWarps.GUI.Type;

import me.Scyy.PrimeWarps.GUI.Sign.SignManager;
import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.Bukkit;
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

    private final boolean validSign;

    public SignGUI(GUI<?> lastGUI, Plugin plugin, Player player, String[] text) {
        this.lastGUI = lastGUI;
        this.plugin = plugin;
        this.player = player;
        this.signManager = plugin.getSignManager();

        this.signID = signManager.createSign(this, text);

        validSign = signID >= 0;
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
        if (!validSign) {
            plugin.getCFH().getPlayerMessenger().msg(player, "errorMessages.signNotAvailable");
            lastGUI.open(player);
        } else {
            signManager.openSign(player, signID);
        }
    }

    public static Listener getListener(Plugin plugin) {
        return new SignListener(plugin);
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

    private static class SignListener implements Listener {
        private final Plugin plugin;
        public SignListener(Plugin plugin) {
            this.plugin = plugin;
        }
        @EventHandler
        public void onSignChangeEvent(SignChangeEvent event) {
            SignManager manager = plugin.getSignManager();
            Sign sign = (Sign) event.getBlock().getState();
            int signID = manager.getSignTag(sign);
            if (signID < 0) return;
            // Sign is a GUI, so cancel the event, remove the sign and interpret the result
            event.setCancelled(true);
            SignGUI oldGUI = manager.getGUI(signID);
            manager.removeSign(signID);

            // Check if the Sign should be closed
            if (oldGUI.shouldClose()) {
                Bukkit.getScheduler().runTask(oldGUI.getPlugin(), () -> oldGUI.getPlayer().closeInventory());
            }
            // handle the interaction
            GUI<?> newGUI = oldGUI.handleInteraction(event);
            if (newGUI instanceof SignGUI) throw new IllegalStateException("Cannot open a Sign GUI from a Sign GUI");
            Bukkit.getScheduler().runTask(oldGUI.getPlugin(), () -> newGUI.open(oldGUI.getPlayer()));
        }

    }

}
