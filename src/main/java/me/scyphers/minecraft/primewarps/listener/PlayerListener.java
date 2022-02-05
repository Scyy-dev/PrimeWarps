package me.scyphers.minecraft.primewarps.listener;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.minecraft.primewarps.warps.WarpRequest;
import me.scyphers.minecraft.primewarps.warps.WarpRequestResponse;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record PlayerListener(PrimeWarps plugin) implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        UUID playerUUID = event.getPlayer().getUniqueId();
        UUID islandUUID = plugin.getSkyblockManager().getIslandUUID(playerUUID);

        // Update warp activity for each warp
        List<Warp> warps = plugin.getWarps().getIslandWarps(islandUUID);
        warps.forEach(warp -> warp.setLastSeen(Instant.now()));

        // Check if the island has any pending warp requests
        List<WarpRequest> warpRequests = plugin.getWarpRequests().getRequests(islandUUID);
        if (warpRequests.size() != 0) {
            plugin.getMessenger().chat(this.getWarpRequestAudience(), "warpMessages.playerHasWarpRequest", "%player%", player.getName());
        }

        // Check if the player has any warp request responses
        if (plugin.getResponseManager().hasResponse(playerUUID)) {

            // Only get responses that the player had - players from the same island will not trigger a response
            List<WarpRequestResponse> responses = plugin.getResponseManager().getResponses(playerUUID);
            responses = responses.stream().filter(response -> response.warpRequester().equals(playerUUID)).collect(Collectors.toList());

            ItemStack warpShard = plugin.getFileManager().getMiscDataFile().getWarpToken();
            int warpCreateCost = plugin.getSettings().getCreateWarpCost();

            int delay = 400;
            for (WarpRequestResponse response : responses) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    boolean refunded = response.refundShardsFor(player, warpShard, warpCreateCost);
                    if (refunded) {
                        plugin.getMessenger().chat(player, "warpMessages.warpShardsRefunded", "%warp%", response.warpName());
                    }
                }, delay);
                delay += 20;
            }

            plugin.getResponseManager().clearResponses(playerUUID);

        }

    }

    private Audience getWarpRequestAudience() {
        List<Player> applicablePlayers = plugin.getServer().getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("primewarps.warps.requests"))
                .collect(Collectors.toList());
        return Audience.audience(applicablePlayers);
    }

}
