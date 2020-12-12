package me.Scyy.PrimeWarps;

import me.Scyy.PrimeWarps.Warps.WarpRequestHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;

public class JoinEvent implements Listener {

    private final Plugin plugin;

    public JoinEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    public void onPlayerJoinEvent(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        Set<WarpRequestHandler> handlers = plugin.getWarpRegister().getRequestHandlerList(player.getUniqueId());

        if (handlers == null) return;

        for (WarpRequestHandler handler : handlers) {

            // Check if the handler has no tasks and if so remove it
            if (handler.getRequestMessage() == null && !handler.isRefundWarpShards()) {

                plugin.getWarpRegister().removeWarpHandler(handler);

            }

            if (handler.getRequestMessage() != null) {

                String message = ChatColor.translateAlternateColorCodes('&' ,handler.getRequestMessage());
                Bukkit.getScheduler().runTaskLater(plugin, () -> player.sendMessage(message), 20);

            }

            if (handler.isRefundWarpShards()) {

                Bukkit.getScheduler().runTaskLater(plugin, () -> handler.refundWarpShards(player, plugin), 20);

            }

        }



    }

}
