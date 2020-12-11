package me.Scyy.PrimeWarps.Config;

import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class UserData extends ConfigFile {

    private final ItemStack warpToken;

    public UserData(Plugin plugin) {
        super(plugin, "data.yml");
        this.warpToken = loadWarpToken();
    }

    public ItemStack loadWarpToken() {
        return new ItemBuilder(Material.PRISMARINE_SHARD).enchant(Enchantment.MENDING, 1).build();
    }

    public void saveWarpToken(ItemStack token) {
        throw new UnsupportedOperationException("Needs work");
    }

    public ItemStack getWarpToken() {
        return warpToken;
    }
}
