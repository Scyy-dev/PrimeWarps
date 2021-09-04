package me.Scyy.PrimeWarps;

import me.Scyy.PrimeWarps.Commands.PlayerWarpAdminCommand;
import me.Scyy.PrimeWarps.Commands.WarpRequestCommand;
import me.Scyy.PrimeWarps.Config.ConfigFileHandler;
import me.Scyy.PrimeWarps.Commands.PlayerWarpCommand;
import me.Scyy.PrimeWarps.Event.JoinEvent;
import me.Scyy.PrimeWarps.Event.WorldLoadListener;
import me.Scyy.PrimeWarps.GUI.sign.SignManager;
import me.Scyy.PrimeWarps.GUI.Type.InventoryGUI;
import me.Scyy.PrimeWarps.Warps.WarpRegister;
import me.Scyy.PrimeWarps.GUI.Type.SignGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Plugin extends JavaPlugin {

    private WarpRegister warpRegister;

    private ConfigFileHandler CFH;

    private WorldLoadListener worldLoadListener;

    private SignManager signManager;

    // this is a change

    @Override
    public void onEnable() {
        this.CFH = new ConfigFileHandler(this);
        this.worldLoadListener = new WorldLoadListener(this);

        // Check if the warps can be loaded
        if (worldLoadListener.allWorldsLoaded()) this.loadWarps();
        else Bukkit.getPluginManager().registerEvents(worldLoadListener, this);

        this.signManager = new SignManager(this);

        // Register all events
        Bukkit.getPluginManager().registerEvents(new JoinEvent(this), this);
        Bukkit.getPluginManager().registerEvents(InventoryGUI.getListener(), this);

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

        // If it has been more than a week since the last week shift was performed. Assumes plugin is regularly enabled/disabled.
        Instant weekTimer = CFH.getMiscDataStorage().getWeekTimer();
        // Weeks are 6.5 days long - this accommodates for irregular server restarts
        Instant weekLater = weekTimer.plus(7, ChronoUnit.DAYS).minus(12, ChronoUnit.HOURS);
        if (Instant.now().isAfter(weekLater)) {
            this.getLogger().info("Detected need to update weekly warp visits. Updating...");
            warpRegister.forceNewWeek();
            CFH.getMiscDataStorage().setWeekTimer(Instant.now());
            this.getLogger().info("Updated.");
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
