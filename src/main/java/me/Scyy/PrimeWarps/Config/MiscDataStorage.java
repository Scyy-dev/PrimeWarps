package me.Scyy.PrimeWarps.Config;

import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class MiscDataStorage extends ConfigFile {

    public MiscDataStorage(Plugin plugin) {
        super(plugin, "data.yml");
    }

    public ItemStack getWarpToken() {
        return config.getItemStack("warpToken");
    }

    public void saveWarpToken(ItemStack token) {
        config.set("warpToken", token);
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Error saving warp token to config!");
            e.printStackTrace();
        }
    }

}
