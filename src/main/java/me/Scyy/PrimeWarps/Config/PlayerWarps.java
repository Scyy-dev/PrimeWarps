package me.Scyy.PrimeWarps.Config;

import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Warps.Warp;
import me.Scyy.PrimeWarps.Warps.WarpRequest;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerWarps extends ConfigFile {

    public PlayerWarps(Plugin plugin) {
        super(plugin, "warps.yml");
    }

    public Map<String, Warp> loadWarps() {

        Map<String, Warp> warps = new LinkedHashMap<>();

        ConfigurationSection section = config.getConfigurationSection("warps");
        if (section == null || section.getKeys(false).size() == 0) {
            plugin.getLogger().warning("No warps found in config!");
            return new LinkedHashMap<>();
        }

        for (String warpName : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(section.getString(warpName + ".owner"));
                Location location = section.getLocation(warpName + ".location");
                Date dateCreated = new Date(section.getLong(warpName + ".dateCreated", 0));
                warps.put(warpName, new Warp(warpName, uuid, location, dateCreated));
            } catch (Exception e) {
                plugin.getLogger().severe("Error loading warps!");
                e.printStackTrace();
                return warps;
            }
        }

        return warps;

    }

    public void saveWarps(Map<String, Warp> warps) {

        // Empty the warp data
        config.set("warps", null);

        // Iterate over each warp
        for (String warpName : warps.keySet()) {

            Warp warp = warps.get(warpName);
            config.set("warps." + warpName + ".owner", warp.getOwner().toString());
            config.set("warps." + warpName + ".location", warp.getLocation());
            config.set("warps." + warpName + ".dateCreated", warp.getDateCreated().toInstant().getEpochSecond());

        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Unable to save any warps to the config file! No warps will be loaded next time!");
            e.printStackTrace();
        }


    }

    public Map<String, WarpRequest> loadWarpRequests() {

        Map<String, WarpRequest> warps = new LinkedHashMap<>();

        ConfigurationSection section = config.getConfigurationSection("warps");
        if (section == null || section.getKeys(false).size() == 0) {
            plugin.getLogger().warning("No Warp Requests found in config!");
            return new LinkedHashMap<>();
        }

        for (String warpName : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(section.getString(warpName + ".owner"));
                Location location = section.getLocation(warpName + ".location");
                warps.put(warpName, new WarpRequest(warpName, uuid, location));
            } catch (Exception e) {
                plugin.getLogger().severe("Error loading warps!");
                e.printStackTrace();
                return warps;
            }
        }

        return warps;



    }

    public void saveWarpRequests(Map<String, WarpRequest> warpRequests) {

        // Empty the warp data
        config.set("warpRequests", null);

        // Iterate over each warp
        for (String warpName : warpRequests.keySet()) {

            WarpRequest warpRequest = warpRequests.get(warpName);
            config.set("warpRequests." + warpName + ".owner", warpRequest.getOwner().toString());
            config.set("warpRequests." + warpName + ".location", warpRequest.getLocation());

        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Unable to save any warps to the config file! No warps will be loaded next time!");
            e.printStackTrace();
        }

    }


}
