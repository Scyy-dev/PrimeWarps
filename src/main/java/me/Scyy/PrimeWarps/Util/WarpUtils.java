package me.Scyy.PrimeWarps.Util;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Warps.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class WarpUtils {

    /**
     * Teleports a player safely to the location specified by the warp
     * @param player the player to teleport
     * @param plugin plugin for scheduling the teleport through Bukkit
     * @param warp the warp that holds the location
     */
    public static void warp(Player player, Plugin plugin, Warp warp) {
        warp(player, plugin, warp, 1);
    }

    /**
     * Teleports a player safely to the location specified by the warp
     * @param player the player to teleport
     * @param plugin plugin for scheduling the teleport through Bukkit
     * @param warp the warp that holds the location
     * @param delay the delay in ticks before the player is warped
     */
    public static void warp(Player player, Plugin plugin, Warp warp, int delay) {

        Location location = warp.getLocation();

        PlayerMessenger pm = plugin.getCFH().getPlayerMessenger();

        if (location.getWorld() == null) {
            pm.msg(player, "errorMessages.worldNotFound");
            return;
        }

        World world = location.getWorld();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Verify the space the player takes up is safe
            if (!world.getBlockAt(location).isPassable() || !world.getBlockAt(location.getBlockX(), location.getBlockY() + 1, location.getBlockZ()).isPassable()) {
                pm.msg(player, "warpMessages.spaceBlocked", false, "%warp%", warp.getName());
                return;
            }

            if (world.getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ()).isPassable()) {
                pm.msg(player, "warpMessages.holeInFloor", false, "%warp%", warp.getName());
                return;
            }

            player.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
            pm.msg(player, "warpMessages.playerWarped", false, "%warp%", warp.getName());
            warp.getUniqueVisitors().add(player.getUniqueId());
        }, delay);




    }

}
