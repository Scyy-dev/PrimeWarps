package me.Scyy.PrimeWarps.Config;

import me.Scyy.PrimeWarps.Plugin;

public class Settings extends ConfigFile {

    public Settings(Plugin plugin) {
        super(plugin, "config.yml");
    }

    public int getWarpTokenCount() {
        return config.getInt("requiredWarpShardAmount", 1);
    }
}
