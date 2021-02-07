package me.Scyy.PrimeWarps.Event;

import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Warps.Warp;
import me.Scyy.PrimeWarps.Warps.WarpRequest;
import me.Scyy.PrimeWarps.Warps.WarpRequestHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Instant;
import java.util.Set;

public class JoinEvent implements Listener {

    private final Plugin plugin;

    public JoinEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (plugin.getWarpRegister() == null) {
            return;
        }

        Set<WarpRequestHandler> handlers = plugin.getWarpRegister().getRequestHandlerList(player.getUniqueId());

        // Check if the player owns any warps and if so update when the owner was last seen
        for (Warp warp : plugin.getWarpRegister().getWarps().values()) {

            if (warp.getOwner().equals(player.getUniqueId())) {
                warp.setOwnerLastSeen(Instant.now());
            }

        }

        // Cycle through warp requests and if the player has a warp request notify staff
        for (WarpRequest request : plugin.getWarpRegister().getWarpRequests().values()) {
            if (request.getOwner().equals(player.getUniqueId())) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.hasPermission("pwarp.admin.request")) {
                        plugin.getCFH().getPlayerMessenger().msg(online, "warpMessages.playerHasWarpRequest", "%player%", player.getName());
                    }
                }
            }

        }

        // Alert the player of any inactive warps they have
        for (Warp warp : plugin.getWarpRegister().getWarps().values()) {
            if (warp.isInactive() && warp.getOwner().equals(player.getUniqueId())) {
                plugin.getCFH().getPlayerMessenger().msg(player, "warpMessages.alertWarpInactive",
                        "%warp%", warp.getName());
            }
        }

        if (handlers == null) return;

        // Check for any messages and shard refunds
        for (WarpRequestHandler handler : handlers) {

            if (handler.isRefundWarpShards()) {

                Bukkit.getScheduler().runTaskLater(plugin, () -> handler.refundWarpShards(player, plugin), 20);

            }

            if (handler.getRequestMessage() != null) {

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.sendMessage(handler.getRequestMessage());
                    handler.setRequestMessage(null);
                }, 20);

            }

        }

        // Clean out used handlers
        plugin.getWarpRegister().filterHandlers(player);

    }

}
