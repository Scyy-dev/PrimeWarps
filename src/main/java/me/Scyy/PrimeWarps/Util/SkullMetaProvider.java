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

public class SkullMetaProvider {

    private final static Map<UUID, OfflinePlayer> players = new HashMap<>();

    public static void setOwner(Plugin plugin, UUID uuid, ItemMeta meta) {
        if (!(meta instanceof SkullMeta)) {
            throw new IllegalArgumentException("Meta must be SkullMeta");
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (players.containsKey(uuid)) {
                ((SkullMeta) meta).setOwningPlayer(players.get(uuid));
                return;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            players.put(uuid, player);
            ((SkullMeta) meta).setOwningPlayer(player);
        });
    }

}
