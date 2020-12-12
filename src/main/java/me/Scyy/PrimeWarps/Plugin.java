package me.Scyy.PrimeWarps;

import me.Scyy.PrimeWarps.Commands.PlayerWarpAdminCommand;
import me.Scyy.PrimeWarps.Commands.WarpRequestCommand;
import me.Scyy.PrimeWarps.Config.ConfigFileHandler;
import me.Scyy.PrimeWarps.Commands.PlayerWarpCommand;
import me.Scyy.PrimeWarps.GUI.GUIListener;
import me.Scyy.PrimeWarps.Warps.WarpRegister;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    private WarpRegister warpRegister;

    private ConfigFileHandler CFH;

    @Override
    public void onEnable() {
        this.CFH = new ConfigFileHandler(this);
        this.warpRegister = new WarpRegister(this, CFH.getPlayerWarps().loadWarps(),
                CFH.getPlayerWarps().loadWarpRequests(),
                CFH.getPlayerWarps().loadWarpHandlers());

        // Register the GUI listener
        Bukkit.getPluginManager().registerEvents(new GUIListener(this), this);

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

    public void reload(CommandSender sender) {
        try {
            CFH.getPlayerWarps().saveWarps(warpRegister.getWarps());
            CFH.getPlayerWarps().saveWarpRequests(warpRegister.getWarpRequests());
            CFH.getPlayerWarps().saveWarpHandlers(warpRegister.getRequestHandlerMap());
            CFH.reloadConfigs();
            sender.sendMessage("Successfully reloaded!");
        } catch (Exception e) {
            sender.sendMessage("Error reloading! Check console for logs!");
            e.printStackTrace();
        }
    }

    public void splashText(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lPrime&7&lWarps"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Built by &3_Scyy"));
    }
}
