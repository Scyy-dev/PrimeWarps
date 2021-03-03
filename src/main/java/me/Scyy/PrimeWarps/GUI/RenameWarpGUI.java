package me.Scyy.PrimeWarps.GUI;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.GUI.Type.GUI;
import me.Scyy.PrimeWarps.GUI.Type.SignGUI;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemStackUtils;
import me.Scyy.PrimeWarps.Util.WarpUtils;
import me.Scyy.PrimeWarps.Warps.Warp;
import me.Scyy.PrimeWarps.Warps.WarpRequest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Locale;

public class RenameWarpGUI extends SignGUI {

    private final Warp warp;

    public RenameWarpGUI(GUI<?> lastGUI, Plugin plugin, Player player, Warp warp) {
        super(lastGUI, plugin, player);
        this.warp = warp;
    }

    @Override
    public @NotNull GUI<?> handleInteraction(SignChangeEvent event) {

        PlayerMessenger pm = plugin.getCFH().getPlayerMessenger();

        // Cancel the event to prevent the real sign being modified
        event.setCancelled(true);

        boolean validSignInput;

        String rawWarpName = event.getLine(0);

        // Verify if the user has only entered a warp name and not modified the sign
        validSignInput = WarpUtils.validName(rawWarpName);

        if (!validSignInput) {
            plugin.getCFH().getPlayerMessenger().msg(player, "warpMessages.invalidWarpName",
                    "%warp%", rawWarpName);
            return new WarpManagerGUI(this, plugin, player, warp);
        }

        String warpName = rawWarpName.toLowerCase(Locale.ROOT);

        // Verify the warp has a valid name
        if (!WarpUtils.validName(warpName)) {
            pm.msg(player, "warpMessages.invalidWarpName", "%warp%", warpName);
            return new WarpManagerGUI(this, plugin, player, warp);
        }

        // Verify the warp doesn't exist yet
        Warp existingWarp = plugin.getWarpRegister().getWarp(warpName);
        if (existingWarp != null) {
            pm.msg(player, "warpMessages.warpAlreadyExists", "%warp%", warpName);
            return new WarpManagerGUI(this, plugin, player, warp);
        }
        WarpRequest existingRequest = plugin.getWarpRegister().getWarpRequest(warpName);
        if (existingRequest != null) {
            pm.msg(player, "warpMessages.warpRequestAlreadyExists", "%warp%", warpName);
            return new WarpManagerGUI(this, plugin, player, warp);
        }

        // Verify the player has enough warp shards
        ItemStack warpShard = plugin.getCFH().getMiscDataStorage().getWarpToken();
        int cost = plugin.getCFH().getSettings().getCreateWarpCost();

        if (!player.getInventory().containsAtLeast(warpShard, cost)) {
            pm.msg(player, "warpMessages.notEnoughWarpShards", "%warp%", warpName);
            return new WarpManagerGUI(this, plugin, player, warp);
        }

        // Remove the warp shards
        ItemStackUtils.removeItem(player, warpShard, cost);

        // Create the new warp
        Warp newWarp = new Warp(warpName, warp.getOwner(), warp.getLocation(), warp.getCategory(), warp.getDateCreated(), Instant.now(), warp.isInactive(), warp.getUniqueVisitors(), warp.getWeeklyVisitors());

        // Update the warp
        plugin.getWarpRegister().updateWarp(warp, newWarp);

        // Let the player know the warp was renamed successfully
        pm.msg(player, "warpMessages.warpRenamed", "%warp%", warpName);

        return new WarpManagerGUI(this, plugin, player, warp);

    }
}
