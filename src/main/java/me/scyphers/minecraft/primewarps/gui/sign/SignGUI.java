package me.scyphers.minecraft.primewarps.gui.sign;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.scycore.config.MessengerFile;
import me.scyphers.scycore.gui.GUI;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SignGUI implements GUI<SignChangeEvent> {

    protected final GUI<?> lastGUI;

    protected final PrimeWarps plugin;

    protected final Player player;

    private final SignManager signManager;

    private final int signID;

    private final String[] text;

    public SignGUI(GUI<?> lastGUI, PrimeWarps plugin, Player player, String[] text) {
        this.lastGUI = lastGUI;
        this.plugin = plugin;
        this.player = player;
        this.signManager = plugin.getSignManager();
        if (text.length != 4) throw new IllegalArgumentException("Must have 4 lines for sign text");
        this.text = text;
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
            lastGUI.open(player);
        } else {
            Sign sign = signManager.getSign(signID);

            for (int i = 0; i < 4; i++) {
                sign.line(i, MessengerFile.toMessageComponent(text[i]));
            }

            player.openSign(sign);
        }
    }

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

    @Override
    public boolean shouldClose() {
        return false;
    }

}
