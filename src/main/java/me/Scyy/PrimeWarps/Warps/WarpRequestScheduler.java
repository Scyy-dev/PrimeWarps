package me.Scyy.PrimeWarps.Warps;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WarpRequestScheduler {

    private final Plugin plugin;

    public WarpRequestScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Schedules a message to be sent to a player that the warp has been approved/rejected.
     * Rejected warps have the warp shards refunded and a message sent.
     * Approved warps have a message sent.
     * @param request The request that was approved/rejected
     * @param type description of the status of request, one of 'rejected' OR 'approved'
     */
    public void scheduleHandler(WarpRequest request, String type) {
        Player player = Bukkit.getPlayer(request.getOwner());
        if (player == null) this.scheduleOfflinePlayer(request, type);
        else this.scheduleOnlineHandler(request, player, type);
    }

    public void scheduleOnlineHandler(WarpRequest request, Player player, String type) {
        PlayerMessenger pm = plugin.getCFH().getPlayerMessenger();
        pm.msg(player, "warpMessages.request" + type, "%warp%", request.getName());

        // Check if the player needs to be refunded for the shards
        if (type.equalsIgnoreCase("rejected")) {
            WarpRequestHandler handler = new WarpRequestHandler(request.getOwner(), request.getName(), true, null);
            plugin.getWarpRegister().addWarpHandler(request.getOwner(), handler);
            handler.refundWarpShards(player, plugin);
        }


    }

    public void scheduleOfflinePlayer(WarpRequest request, String type) {
        boolean refundShards = type.equalsIgnoreCase("rejected");
        String requestMessage = plugin.getCFH().getPlayerMessenger().getMsg("warpMessages.request" + type, "%warp%", request.getName());
        WarpRequestHandler handler = new WarpRequestHandler(request.getOwner(), request.getName(), refundShards, requestMessage);
        plugin.getWarpRegister().addWarpHandler(request.getOwner(), handler);
    }
}
