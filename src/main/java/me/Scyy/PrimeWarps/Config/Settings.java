package me.Scyy.PrimeWarps.Config;

import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.ChatColor;

public class Settings extends ConfigFile {

    public Settings(Plugin plugin) {
        super(plugin, "config.yml");
    }

    public int getWarpTokenCount() {
        return config.getInt("requiredWarpShardAmount", 1);
    }

    public String getWarpMessage(String warpName) {
        String rawMessage = config.getString("warpName");
        if (rawMessage == null) return "ERROR_WARP_NAMING_NOT_FOUND";
        rawMessage = rawMessage.replaceAll("%warp%", warpName);
        return ChatColor.translateAlternateColorCodes('&', rawMessage);
    }
}
