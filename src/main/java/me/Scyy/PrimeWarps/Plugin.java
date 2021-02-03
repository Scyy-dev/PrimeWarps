package me.Scyy.PrimeWarps;

import me.Scyy.PrimeWarps.Commands.PlayerWarpAdminCommand;
import me.Scyy.PrimeWarps.Commands.WarpRequestCommand;
import me.Scyy.PrimeWarps.Config.ConfigFileHandler;
import me.Scyy.PrimeWarps.Commands.PlayerWarpCommand;
import me.Scyy.PrimeWarps.Event.JoinEvent;
import me.Scyy.PrimeWarps.Event.WorldLoadListener;
import me.Scyy.PrimeWarps.GUI.Sign.SignManager;
import me.Scyy.PrimeWarps.GUI.Type.InventoryGUI;
import me.Scyy.PrimeWarps.Warps.WarpRegister;
import me.Scyy.PrimeWarps.GUI.Type.SignGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    private WarpRegister warpRegister;

    private ConfigFileHandler CFH;

    private WorldLoadListener worldLoadListener;

    private SignManager signManager;

    @Override
    public void onEnable() {
        this.CFH = new ConfigFileHandler(this);
        this.worldLoadListener = new WorldLoadListener(this);

        // Check if the warps can be loaded
        if (worldLoadListener.allWorldsLoaded()) this.loadWarps();
        else Bukkit.getPluginManager().registerEvents(worldLoadListener, this);

        this.signManager = new SignManager(this);

        // Register the GUI listener
        Bukkit.getPluginManager().registerEvents(InventoryGUI.getListener(), this);
        Bukkit.getPluginManager().registerEvents(SignGUI.getListener(this), this);

        // Register all events
        Bukkit.getPluginManager().registerEvents(new JoinEvent(this), this);

        // Register all the commands
        PlayerWarpCommand playerWarpCommand = new PlayerWarpCommand(this);
        WarpRequestCommand warpRequestCommand = new WarpRequestCommand(this);
        PlayerWarpAdminCommand playerWarpAdminCommand = new PlayerWarpAdminCommand(this);
        this.getCommand("pwarp").setExecutor(playerWarpCommand);
        this.getCommand("pwarp").setTabCompleter(playerWarpCommand);
        this.getCommand("warprequest").setExecutor(warpRequestCommand);
        this.getCommand("warprequest").setTabCompleter(warpRequestCommand);
        this.getCommand("pwadmin").setExecutor(playerWarpAdminCommand);
        this.getCommand("pwadmin").setTabCompleter(playerWarpAdminCommand);

        // Ensure that the Warp categories has the 'default' category active, or send a warning
        String defaultCategory = CFH.getSettings().getDefaultCategory();
        if (!CFH.getSettings().getCategories().contains(defaultCategory)) {
            getLogger().warning("Could not find the default category for warps!");
        }

    }

    @Override
    public void onDisable() {
        this.getLogger().info("Saving warp data to config...");
        try {
            CFH.getPlayerWarps().saveWarps(warpRegister.getWarps());
            CFH.getPlayerWarps().saveWarpRequests(warpRegister.getWarpRequests());
            CFH.getPlayerWarps().saveWarpHandlers(warpRegister.getRequestHandlerMap());
            this.getLogger().info("Warp data saved!");
        } catch (Exception e) {
            this.getLogger().severe("Could not save warp data!");
            e.printStackTrace();
        }
    }

    public ConfigFileHandler getCFH() {
        return CFH;
    }

    public WarpRegister getWarpRegister() {
        return warpRegister;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public boolean allWorldsLoaded() {
        return worldLoadListener.allWorldsLoaded();
    }

    public void loadWarps() {
        this.warpRegister = new WarpRegister(this, CFH.getPlayerWarps().loadWarps(),
                CFH.getPlayerWarps().loadWarpRequests(),
                CFH.getPlayerWarps().loadWarpHandlers());

        // Iterate over every warp and mark them as inactive if needed
        warpRegister.updateWarpInactivity(CFH.getSettings().getInactiveDayMeasure());
        this.getLogger().info("Warp data loaded");
    }

    public void reload(CommandSender sender) {
        try {
            CFH.getPlayerWarps().saveWarps(warpRegister.getWarps());
            CFH.getPlayerWarps().saveWarpRequests(warpRegister.getWarpRequests());
            CFH.getPlayerWarps().saveWarpHandlers(warpRegister.getRequestHandlerMap());
            CFH.reloadConfigs();
            signManager.loadSigns();
            if (CFH.getSettings().getCategoryMaterial("default") == null) {
                sender.sendMessage("'default' warp category not found!");
            }
            sender.sendMessage("Successfully reloaded!");
        } catch (Exception e) {
            sender.sendMessage("Error reloading! Check console for logs!");
            e.printStackTrace();
        }
    }

    public void splashText(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lPrime&7&lWarps &6v" + this.getDescription().getVersion()));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Built by &3_Scyy"));
    }
}
