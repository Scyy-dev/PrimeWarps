package me.scyphers.minecraft.primewarps.command;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.util.ItemStackUtils;
import me.scyphers.minecraft.primewarps.warps.WarpRequest;
import me.scyphers.scycore.api.Messenger;
import me.scyphers.scycore.command.SimpleCommandFactory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class WarpRequestCommandFactory extends SimpleCommandFactory {

    private final PrimeWarps plugin;

    private final Messenger m;

    public WarpRequestCommandFactory(PrimeWarps plugin) {
        super(plugin, "warprequest", sender -> plugin.getMessenger().sendChat(sender, "warpMessages.requestHelp"));
        this.plugin = plugin;
        this.m = plugin.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {

        if (!sender.hasPermission("primewarps.request")) {
            m.chat(sender, "errorMessages.noPermission");
            return true;
        }

        if (!(sender instanceof Player player)) {
            m.chat(sender, "errorMessages.mustBePlayer");
            return true;
        }

        UUID islandUUID = plugin.getSkyblockManager().getIslandUUID(player.getUniqueId());

        // Ensure the player has the right island role
        if (!plugin.getSkyblockManager().hasAtLeast(player.getUniqueId(), "TRUSTED")) {
            m.chat(sender, "errorMessages.noIslandPermission", "%player%", player.getName());
            return true;
        }

        // Check if the island has reached the warp cap for the current prestige
        int maxWarps = plugin.getSettings().getMaximumWarps();
        int prestigePerWarp = plugin.getSettings().getPrestigePerWarp();
        long warpCount = plugin.getWarps().getWarpCount(islandUUID);
        int prestigeLevel = plugin.getSkyblockManager().getPrestigeLevel(islandUUID);

        // The number of warps the player can have at their current prestige level
        long maximumPrestigeWarps = prestigeLevel / prestigePerWarp;

        // If the player has reached the hard warp count cap or reached the cap for their current prestige
        if (warpCount >= maxWarps || warpCount >= maximumPrestigeWarps) {
            m.chat(sender, "errorMessages.warpCapReached", "%prestige%", "" + prestigeLevel, "%warpCap%", "" + Math.min(maxWarps, maximumPrestigeWarps));
            return true;
        }

        String formatName = args[0].toLowerCase(Locale.ROOT);

        // Check if the player has the required items
        int warpCost = plugin.getSettings().getCreateWarpCost();
        ItemStack warpToken = plugin.getFileManager().getMiscDataFile().getWarpToken();
        if (!player.getInventory().containsAtLeast(warpToken, warpCost)) {
            m.chat(sender, "warpMessages.notEnoughWarpShards", "%warp%", formatName);
            return true;
        }

        // Check if the warp already exists
        if (plugin.getWarps().warpExists(args[0])) {
            m.chat(sender, "warpMessages.warpAlreadyExists", "%warp%", formatName);
            return true;
        }
        if (plugin.getWarpRequests().warpRequestExists(args[0])) {
            m.chat(sender, "warpMessages.warpRequestAlreadyExists", "%warp%", formatName);
            return true;
        }

        // Ensure the warp is on the players island
        if (!plugin.getSkyblockManager().isWithinPlayerIsland(player.getUniqueId(), player.getLocation())) {
            m.chat(player, "errorMessages.mustBeOnPlayerIsland", "%player%", player.getName());
            return true;
        }

        // Create the Warp Request
        WarpRequest request = new WarpRequest(formatName, islandUUID, player.getUniqueId(), player.getLocation(), "", Instant.now());
        boolean requestSuccess = plugin.getWarpRequests().addRequest(formatName, request);
        if (requestSuccess) {

            // Message the player that the request was successful
            m.chat(sender, "warpMessages.warpRequestAdded", "%warp%", formatName);

            // Remove the tokens from the players inventory
            ItemStack requiredTokens = warpToken.clone();
            ItemStackUtils.removeItem(player, requiredTokens, plugin.getSettings().getCreateWarpCost());

        } else {
            m.chat(sender, "warpMessages.warpAlreadyExists", "%warp%", formatName);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull String[] strings) {
        return Collections.emptyList();
    }

}
