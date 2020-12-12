package me.Scyy.PrimeWarps.Config;

import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Warps.Warp;
import me.Scyy.PrimeWarps.Warps.WarpRequestHandler;
import me.Scyy.PrimeWarps.Warps.WarpRequest;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.*;

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
                Set<UUID> uniqueVisitors = new LinkedHashSet<>();
                for (String visitor : section.getStringList(warpName + ".uniqueVisitors")) {
                    uniqueVisitors.add(UUID.fromString(visitor));
                }
                warps.put(warpName, new Warp(warpName, uuid, location, dateCreated, uniqueVisitors));
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
            List<String> stringUUIDs = new LinkedList<>();
            for (UUID uuid : warp.getUniqueVisitors()) {
                stringUUIDs.add(uuid.toString());
            }
            config.set("warps." + warpName + ".uniqueVisitors", stringUUIDs);

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
                plugin.getLogger().severe("Error loading warp requests! Any pending warp requests have been lost!");
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

    public Map<UUID, Set<WarpRequestHandler>> loadWarpHandlers() {

        Map<UUID, Set<WarpRequestHandler>> requestHandlerMap = new LinkedHashMap<>();

        ConfigurationSection section = config.getConfigurationSection("requestHandlers");
        if (section == null || section.getKeys(false).size() == 0) {
            plugin.getLogger().warning("No warp request handlers in config");
            return new LinkedHashMap<>();
        }

        // Iterate over the Players
        for (String rawOwner : section.getKeys(false)) {

            ConfigurationSection warps = section.getConfigurationSection(rawOwner);

            if (warps == null) continue;

            UUID owner = UUID.fromString(rawOwner);

            Set<WarpRequestHandler> requestHandlers = new LinkedHashSet<>();

            // For each player, iterate over every warp handler they have
            for (String warpName : warps.getKeys(false)) {
                try {
                    boolean refundShards = warps.getBoolean(warpName + ".refundShards");
                    String requestMessage = warps.getString(warpName + ".requestMessage");
                    requestHandlers.add(new WarpRequestHandler(owner, warpName, refundShards, requestMessage));
                } catch (Exception e) {
                    plugin.getLogger().warning("Error loading warp request handlers! Any pending warp shard refunds were lost!");
                    e.printStackTrace();
                }

            }

            requestHandlerMap.put(owner, requestHandlers);

        }

        return requestHandlerMap;

    }

    public void saveWarpHandlers(Map<UUID, Set<WarpRequestHandler>> warps) {

        // Empty the warp data
        config.set("warpHandlers", null);

        // Iterate over each owner
        for (UUID uuid : warps.keySet()) {
            String rawOwner = uuid.toString();

            // Iterate over each request handler the owner has
            for (WarpRequestHandler handler : warps.get(uuid)) {
                config.set("warpHandlers." + rawOwner + handler.getWarpName() + ".refundShards", handler.isRefundWarpShards());
                config.set("warpHandlers." + rawOwner + handler.getWarpName() + ".requestMessage", handler.getRequestMessage());
            }
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Unable to save any warp handlers to config! Pending warp shard refunds have been lost!");
            e.printStackTrace();
        }


    }


}
