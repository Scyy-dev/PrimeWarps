package me.Scyy.PrimeWarps.Util;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Warps.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.regex.Pattern;

public class WarpUtils {

    public static final Pattern warpNamePattern = Pattern.compile("^[a-zA-Z0-9]+$");

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

            // Check if the player is on a full block
            boolean isOnFullBlock = location.getY() % 1 == 0;

            System.out.println("full block: " + isOnFullBlock);

            // Block the player stands on
            Block b1 = isOnFullBlock ? world.getBlockAt(location.getBlockX(), (int) Math.floor(location.getBlockY() - 1), location.getBlockZ())
                    : world.getBlockAt(location.getBlockX(), (int) Math.floor(location.getBlockY()), location.getBlockZ());

            // Block the player is in (legs)
            Block b2 = isOnFullBlock ? world.getBlockAt(location.getBlockX(), (int) Math.floor(location.getBlockY()), location.getBlockZ())
                    : world.getBlockAt(location.getBlockX(), (int) Math.floor(location.getBlockY() + 1), location.getBlockZ());

            // Block the player is in (head)
            Block b3 = isOnFullBlock ? world.getBlockAt(location.getBlockX(), (int) Math.floor(location.getBlockY() + 1), location.getBlockZ())
                    : world.getBlockAt(location.getBlockX(), (int) Math.floor(location.getBlockY() + 2), location.getBlockZ());

            System.out.println(b1.getType() + " Loc: " + b1.getLocation());
            System.out.println(b2.getType() + " Loc: " + b2.getLocation());
            System.out.println(b3.getType() + " Loc: " + b3.getLocation());

            // Verify the space the player takes up is safe
            if (!b2.isPassable() || !b3.isPassable()) {
                pm.msg(player, "warpMessages.spaceBlocked", "%warp%", warp.getName());
                return;
            }

            // Verify the player has a block to stand on
            if (b1.isPassable()) {
                pm.msg(player, "warpMessages.holeInFloor", "%warp%", warp.getName());
                return;
            }

            player.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
            pm.msg(player, "warpMessages.playerWarped", "%warp%", warp.getName());
            warp.getUniqueVisitors().add(player.getUniqueId());
        }, delay);

    }

    public static boolean validName(String name) {
        if (name == null) return false;
        return warpNamePattern.matcher(name).matches();
    }

}
