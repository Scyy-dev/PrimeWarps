package me.Scyy.PrimeWarps.GUI;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemStackUtils;
import me.Scyy.PrimeWarps.Util.WarpUtils;
import me.Scyy.PrimeWarps.Warps.Warp;
import me.Scyy.PrimeWarps.Warps.WarpRequest;
import me.Scyy.PrimeWarps.GUI.Type.GUI;
import me.Scyy.PrimeWarps.GUI.Type.SignGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class CreateWarpGUI extends SignGUI {

    public CreateWarpGUI(GUI<?> lastGUI, Plugin plugin, Player player) {
        super(lastGUI, plugin, player);
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
            return new FeaturedWarpsGUI(this, plugin, player);
        }

        String warpName = rawWarpName.toLowerCase(Locale.ROOT);

        // Verify the warp has a valid name
        if (!WarpUtils.validName(warpName)) {
            pm.msg(player, "warpMessages.invalidWarpName", "%warp%", warpName);
            return new FeaturedWarpsGUI(this, plugin, player);
        }

        // Verify the warp doesn't exist yet
        Warp existingWarp = plugin.getWarpRegister().getWarp(warpName);
        if (existingWarp != null) {
            pm.msg(player, "warpMessages.warpAlreadyExists", "%warp%", warpName);
            return new FeaturedWarpsGUI(this, plugin, player);
        }
        WarpRequest existingRequest = plugin.getWarpRegister().getWarpRequest(warpName);
        if (existingRequest != null) {
            pm.msg(player, "warpMessages.warpRequestAlreadyExists", "%warp%", warpName);
            return new FeaturedWarpsGUI(this, plugin, player);
        }

        // Verify the warp is in a valid world
        List<String> permittedWorlds = plugin.getCFH().getSettings().getWorlds();
        if (!permittedWorlds.contains(player.getWorld().getName())) {
            pm.msg(player, "errorMessages.illegalWorldForWarp");
            return new FeaturedWarpsGUI(this, plugin, player);
        }

        // Verify the player has enough warp shards
        ItemStack warpShard = plugin.getCFH().getMiscDataStorage().getWarpToken();
        int cost = plugin.getCFH().getSettings().getCreateWarpCost();

        if (!player.getInventory().containsAtLeast(warpShard, cost)) {
            pm.msg(player, "warpMessages.notEnoughWarpShards", "%warp%", warpName);
            return new FeaturedWarpsGUI(this, plugin, player);
        }

        // Remove the warp shards
        ItemStackUtils.removeItem(player, warpShard, cost);

        // Create the new warp
        WarpRequest warpRequest = new WarpRequest(warpName, player.getName(), player.getUniqueId(), player.getLocation());

        // Add the new warp
        plugin.getWarpRegister().addWarpRequest(warpName, warpRequest);

        // Let the player know the warp request was created
        pm.msg(player, "warpMessages.warpRequestAdded", "%warp%", warpRequest.getName());

        return new FeaturedWarpsGUI(this, plugin, player);

    }
}
