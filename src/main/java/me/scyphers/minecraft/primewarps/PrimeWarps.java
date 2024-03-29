package me.scyphers.minecraft.primewarps;

import me.scyphers.minecraft.primewarps.command.PrimeWarpAdminCommandFactory;
import me.scyphers.minecraft.primewarps.command.PrimeWarpCommandFactory;
import me.scyphers.minecraft.primewarps.command.WarpRequestCommandFactory;
import me.scyphers.minecraft.primewarps.config.PrimeWarpsFileManager;
import me.scyphers.minecraft.primewarps.config.Settings;
import me.scyphers.minecraft.primewarps.external.SkyblockManager;
import me.scyphers.minecraft.primewarps.gui.sign.SignManager;
import me.scyphers.minecraft.primewarps.listener.PlayerListener;
import me.scyphers.minecraft.primewarps.listener.WorldLoadListener;
import me.scyphers.minecraft.primewarps.warps.RequestResponseManager;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.minecraft.primewarps.warps.WarpRegister;
import me.scyphers.minecraft.primewarps.warps.WarpRequestRegister;
import me.scyphers.scycore.BasePlugin;
import me.scyphers.scycore.api.Messenger;
import me.scyphers.scycore.gui.InventoryListener;
import org.bukkit.command.CommandSender;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class PrimeWarps extends BasePlugin {

    private PrimeWarpsFileManager fileManager;

    private AsyncBridge asyncBridge;

    private SkyblockManager skyblockManager;

    private SignManager signManager;

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

        this.asyncBridge = new AsyncBridge(this);

        this.skyblockManager = new SkyblockManager(this);
        if (!skyblockManager.isPluginLoaded()) {
            this.getLogger().severe("Skyblock plugin not found! Warps cannot be linked to islands! Disabling plugin...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialise all commands
        PrimeWarpCommandFactory commandFactory = new PrimeWarpCommandFactory(this);
        PrimeWarpAdminCommandFactory adminCommandFactory = new PrimeWarpAdminCommandFactory(this);
        WarpRequestCommandFactory requestCommandFactory = new WarpRequestCommandFactory(this);

        this.signManager = new SignManager(this);

        // Register all listeners
        this.getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new WorldLoadListener(this), this);

        // Test all warps for inactivity
        int inactiveTimer = this.getSettings().getInactiveDays();
        Instant warpInactiveMarker = Instant.now().minus(inactiveTimer, ChronoUnit.DAYS);
        for (Warp warp : this.getWarps().getAllWarps()) {
            if (warp.getLastSeen().isBefore(warpInactiveMarker)) {
                warp.setInactive(true);
            }
        }

        this.successfulEnable = true;
    }

    @Override
    public void onDisable() {
        if (!successfulEnable) return;

        try {
            this.fileManager.saveAll();
        } catch (Exception e) {
            this.getSLF4JLogger().error("An error occurred while saving configs!", e);
        }

    }

    @Override
    public void reload(CommandSender sender) {
        try {
            fileManager.reloadConfigs();
            sender.sendMessage("Successfully reloaded!");
        } catch (Exception e) {
            sender.sendMessage("Something went wrong reloading configs!");
            getSLF4JLogger().error("An error occurred while reloading configs", e);
        }
    }

    public WarpRegister getWarps() {
        return fileManager.getWarpsFile();
    }

    public WarpRequestRegister getWarpRequests() {
        return fileManager.getWarpRequestFile();
    }

    public RequestResponseManager getResponseManager() {
        return fileManager.getRequestResponseFile();
    }

    public SkyblockManager getSkyblockManager() {
        return skyblockManager;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public AsyncBridge getAsync() {
        return asyncBridge;
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
