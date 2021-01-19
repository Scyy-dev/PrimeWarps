package me.Scyy.PrimeWarps.Config;

import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MiscDataStorage extends ConfigFile {

    public MiscDataStorage(Plugin plugin) {
        super(plugin, "data.yml");
    }

    public ItemStack getWarpToken() {
        return config.getItemStack("warpToken");
    }

    public void saveWarpToken(ItemStack token) {
        config.set("warpToken", token);
        this.saveConfig("Could not save warp token to config");
    }

    public Map<String, Location> getSignLocations() {

        Map<String, Location> locations = new HashMap<>();

        ConfigurationSection signSection = config.getConfigurationSection("signs");

        if (signSection == null) {
            plugin.getLogger().warning("Could not find any sign GUI locations in config! Make sure to set them with /pwa setsigngui");
            return locations;
        }

        for (String worldName : signSection.getKeys(false)) {

            Location loc = signSection.getLocation(worldName);
            locations.put(worldName, loc);

        }

        return locations;
    }

    public void setSignLocation(String worldName, Location location) {
        config.set("signs." + worldName, location);
        this.saveConfig("Could not save sign location to config");
    }

}
