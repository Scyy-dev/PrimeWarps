package me.Scyy.PrimeWarps;

import me.Scyy.PrimeWarps.Warps.Warp;
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

        Set<WarpRequestHandler> handlers = plugin.getWarpRegister().getRequestHandlerList(player.getUniqueId());

        // Check if the player owns any warps and if so update when the owner was last seen
        for (Warp warp : plugin.getWarpRegister().getWarps().values()) {

            if (warp.getOwner().equals(player.getUniqueId())) {
                warp.setOwnerLastSeen(Instant.now());
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

            // Check if the handler has no tasks and if so remove it
            if (handler.getRequestMessage() == null && !handler.isRefundWarpShards()) {

                plugin.getWarpRegister().removeWarpHandler(handler);

            }

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



    }

}
