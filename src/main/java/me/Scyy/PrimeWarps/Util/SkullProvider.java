package me.Scyy.PrimeWarps.Util;

import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkullProvider {

    private static final Map<UUID, SkullMeta> metaMap = new HashMap<>();

    public static ItemMeta getMeta(Plugin plugin, UUID uuid) {
        if (metaMap.containsKey(uuid)) {
            return metaMap.get(uuid).clone();
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            skullMeta.setOwningPlayer(player);
            metaMap.put(uuid, skullMeta);
        });
        return new ItemStack(Material.PLAYER_HEAD).getItemMeta();
    }

    public static void clearHeadData() {
        metaMap.clear();
    }
}
