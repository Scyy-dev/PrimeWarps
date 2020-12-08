package me.Scyy.PrimeWarps.Config;

import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.inventory.ItemStack;

public class UserData extends ConfigFile {

    private final ItemStack warpToken;

    public UserData(Plugin plugin) {
        super(plugin, "data.yml");
        this.warpToken = loadWarpToken();
    }

    public ItemStack loadWarpToken() {
        throw new UnsupportedOperationException("Needs work");
    }

    public void saveWarpToken(ItemStack token) {
        throw new UnsupportedOperationException("Needs work");
    }

    public ItemStack getWarpToken() {
        return warpToken;
    }
}
