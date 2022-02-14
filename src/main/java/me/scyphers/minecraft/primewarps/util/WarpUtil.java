package me.scyphers.minecraft.primewarps.util;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.scycore.api.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class WarpUtil {

    public static final Pattern warpNamePattern = Pattern.compile("^[a-zA-Z0-9]+$");

    /**
     * Teleports a player safely to the location specified by the warp
     * @param player the player to teleport
     * @param plugin plugin for scheduling the teleport through Bukkit
     * @param warp the warp that holds the location
     */
    public static void warp(Player player, PrimeWarps plugin, Warp warp) {
        warp(player, plugin, warp, 1);
    }

    /**
     * Teleports a player safely to the location specified by the warp
     * @param player the player to teleport
     * @param plugin plugin for scheduling the teleport through Bukkit
     * @param warp the warp that holds the location
     * @param delay the delay in ticks before the player is warped
     */
    public static void warp(Player player, PrimeWarps plugin, Warp warp, int delay) {

        Location location = warp.getLocation();
        Messenger m = plugin.getMessenger();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
            m.chat(player, "warpMessages.playerWarped", "%warp%", warp.getName());
            warp.addVisitor(player.getUniqueId());
        }, delay);

    }

    public static boolean canWarp(@NotNull Location location) {

        World world = location.getWorld();

        if (world == null) return false;

        // Check if the player is on a full block
        boolean isOnFullBlock = location.getY() % 1 == 0;

        // Block the player stands on
        Block b1 = isOnFullBlock ? world.getBlockAt(location.getBlockX(), (int) Math.floor(location.getBlockY() - 1), location.getBlockZ())
                : world.getBlockAt(location.getBlockX(), (int) Math.floor(location.getBlockY()), location.getBlockZ());

        // Block the player is in (legs)
        Block b2 = isOnFullBlock ? world.getBlockAt(location.getBlockX(), (int) Math.floor(location.getBlockY()), location.getBlockZ())
                : world.getBlockAt(location.getBlockX(), (int) Math.floor(location.getBlockY() + 1), location.getBlockZ());

        // Block the player is in (head)
        Block b3 = isOnFullBlock ? world.getBlockAt(location.getBlockX(), (int) Math.floor(location.getBlockY() + 1), location.getBlockZ())
                : world.getBlockAt(location.getBlockX(), (int) Math.floor(location.getBlockY() + 2), location.getBlockZ());

        // Verify the space the player takes up is safe
        if (!b2.isPassable() || !b3.isPassable()) {
            return false;
        }

        // Verify the player has a block to stand on
        return !b1.isPassable();

    }

    public static boolean reachedMaxWarps(int maxWarps, int prestigePerWarp, long warpCount, int prestigeLevel) {
        long maximumPrestigeWarps = prestigeLevel / prestigePerWarp;
        return warpCount >= maxWarps || warpCount >= maximumPrestigeWarps;
    }

    public static boolean validName(String name) {
        if (name == null) return false;
        return warpNamePattern.matcher(name).matches();
    }

}
