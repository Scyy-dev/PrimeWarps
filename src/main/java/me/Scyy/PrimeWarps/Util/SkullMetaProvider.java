package me.Scyy.PrimeWarps.Util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class SkullMetaProvider {
    public static ItemMeta getMeta(UUID uuid) {
        SkullMeta meta = (SkullMeta) new ItemStack(Material.PLAYER_HEAD).getItemMeta();
        assert meta != null;
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        meta.setOwningPlayer(player);
        return meta;
    }
}
