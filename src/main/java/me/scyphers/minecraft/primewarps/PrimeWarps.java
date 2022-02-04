package me.scyphers.minecraft.primewarps;

import me.scyphers.minecraft.primewarps.config.PrimeWarpsFileManager;
import me.scyphers.minecraft.primewarps.config.Settings;
import me.scyphers.minecraft.primewarps.external.SkyblockManager;
import me.scyphers.minecraft.primewarps.warps.WarpRegister;
import me.scyphers.minecraft.primewarps.warps.WarpRequestRegister;
import me.scyphers.scycore.BasePlugin;
import me.scyphers.scycore.api.Messenger;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class PrimeWarps extends BasePlugin {

    private PrimeWarpsFileManager fileManager;

    private SkyblockManager skyblockManager;

    private boolean successfulEnable;

    @Override
    public void onEnable() {

        this.successfulEnable = false;

        try {
            this.fileManager = new PrimeWarpsFileManager(this);
        } catch (Exception e) {
            this.getSLF4JLogger().error("Something went wrong loading configs", e);
            return;
        }

        this.skyblockManager = new SkyblockManager(this);
        if (!skyblockManager.isPluginLoaded()) {
            this.getLogger().severe("Skyblock plugin not found! Warps cannot be linked to islands! Disabling plugin...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.successfulEnable = true;
    }

    @Override
    public void onDisable() {
        if (!successfulEnable) return;
    }

    @Override
    public void reload(CommandSender commandSender) {

    }

    public WarpRegister getWarps() {
        return fileManager.getWarpsFile();
    }

    public WarpRequestRegister getWarpRequests() {
        return fileManager.getWarpRequestFile();
    }

    public SkyblockManager getSkyblockManager() {
        return skyblockManager;
    }

    @Override
    public PrimeWarpsFileManager getFileManager() {
        return fileManager;
    }

    @Override
    public Settings getSettings() {
        return fileManager.getSettings();
    }

    @Override
    public Messenger getMessenger() {
        return fileManager.getMessenger();
    }

    @Override
    public List<String> getSplashText() {
        return Arrays.asList(
                "&3&lPrime&b&lWarps &r&8v&6" + this.getDescription().getVersion(),
                "&7Built by &6" + this.getDescription().getAuthors()
        );
    }

    @Override
    public boolean wasSuccessfulEnable() {
        return successfulEnable;
    }
}
