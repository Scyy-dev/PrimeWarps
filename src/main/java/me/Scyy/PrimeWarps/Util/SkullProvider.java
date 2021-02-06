package me.Scyy.PrimeWarps.Util;

import com.destroystokyo.paper.profile.PlayerProfile;
import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkullProvider {

    private static final Map<UUID, PlayerProfile> profiles = new HashMap<>();

    public static ItemMeta getMeta(Plugin plugin, UUID uuid) {
        SkullMeta meta = (SkullMeta) new ItemStack(Material.PLAYER_HEAD).getItemMeta();
        if (profiles.containsKey(uuid)) {
            meta.setPlayerProfile(profiles.get(uuid));
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                PlayerProfile profile = Bukkit.createProfile(uuid);
                profile.complete();
                profiles.put(uuid, profile);
            });
        }
        return meta;
    }

    public static void clearHeadData() {
        profiles.clear();
    }

}
