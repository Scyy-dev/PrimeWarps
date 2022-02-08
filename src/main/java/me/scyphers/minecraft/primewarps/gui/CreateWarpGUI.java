package me.scyphers.minecraft.primewarps.gui;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.gui.sign.SignGUI;
import me.scyphers.minecraft.primewarps.util.ItemStackUtils;
import me.scyphers.minecraft.primewarps.util.WarpUtil;
import me.scyphers.minecraft.primewarps.warps.WarpRequest;
import me.scyphers.scycore.gui.GUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

public class CreateWarpGUI extends SignGUI {

    private static final PlainTextComponentSerializer plainText = PlainTextComponentSerializer.plainText();

    public CreateWarpGUI(GUI<?> lastGUI, PrimeWarps plugin, Player player) {
        super(lastGUI, plugin, player, new String[]{
                "",
                " ^^^^^^^^^^^^^ ",
                "   Enter Warp  ",
                "      Name     "
        });
    }

    @Override
    public @NotNull GUI<?> handleInteraction(SignChangeEvent event) {

        event.setCancelled(true);

        // Get the inputted warp name
        Component rawWarpName = event.line(0);
        String warpName = plainText.serialize( rawWarpName != null ? rawWarpName : Component.empty()).toLowerCase(Locale.ROOT);

        // Ensure the warp name is valid
        if (!WarpUtil.validName(warpName)) {
            plugin.getMessenger().chat(player, "warpMessages.invalidWarpName", "%warp%", warpName);
            return new FeaturedWarpsGUI(null, plugin, player);
        }

        UUID islandUUID = plugin.getSkyblockManager().getIslandUUID(player.getUniqueId());

        // Ensure the player has the right island role
        if (!plugin.getSkyblockManager().hasAtLeast(player.getUniqueId(), "TRUSTED")) {
            plugin.getMessenger().chat(player, "errorMessages.noIslandPermission", "%player%", player.getName());
            return new FeaturedWarpsGUI(null, plugin, player);
        }

        // Check if the island has reached the warp cap for the current prestige
        int maxWarps = plugin.getSettings().getMaximumWarps();
        int prestigePerWarp = plugin.getSettings().getPrestigePerWarp();
        long warpCount = plugin.getWarps().getWarpCount(islandUUID);
        int prestigeLevel = plugin.getSkyblockManager().getPrestigeLevel(islandUUID);

        long maximumPrestigeWarps = prestigeLevel / prestigePerWarp;

        // If the player has reached the hard warp count cap or reached the cap for their current prestige
        if (WarpUtil.reachedMaxWarps(maxWarps, prestigePerWarp, warpCount, prestigeLevel)) {
            plugin.getMessenger().chat(player, "errorMessages.warpCapReached", "%prestige%", "" + prestigeLevel, "%warpCap%", "" + Math.min(maxWarps, maximumPrestigeWarps));
            return new FeaturedWarpsGUI(null, plugin, player);
        }

        // Ensure the warp is on the players island
        if (!plugin.getSkyblockManager().isWithinPlayerIsland(player.getUniqueId(), player.getLocation())) {
            plugin.getMessenger().chat(player, "errorMessages.mustBeOnPlayerIsland", "%player%", player.getName());
            return new FeaturedWarpsGUI(null, plugin, player);
        }

        // Check if the player has the required items
        int warpCost = plugin.getSettings().getCreateWarpCost();
        ItemStack warpToken = plugin.getFileManager().getMiscDataFile().getWarpToken();
        if (!player.getInventory().containsAtLeast(warpToken, warpCost)) {
            plugin.getMessenger().chat(player, "warpMessages.notEnoughWarpShards", "%warp%", warpName);
            return new FeaturedWarpsGUI(null, plugin, player);
        }

        // Check if the warp already exists
        if (plugin.getWarps().warpExists(warpName)) {
            plugin.getMessenger().chat(player, "warpMessages.warpAlreadyExists", "%warp%", warpName);
            return new FeaturedWarpsGUI(null, plugin, player);
        }
        if (plugin.getWarpRequests().warpRequestExists(warpName)) {
            plugin.getMessenger().chat(player, "warpMessages.warpRequestAlreadyExists", "%warp%", warpName);
            return new FeaturedWarpsGUI(null, plugin, player);
        }

        // Create the Warp Request
        WarpRequest request = new WarpRequest(warpName, islandUUID, player.getUniqueId(), player.getLocation(), "", Instant.now());
        boolean requestSuccess = plugin.getWarpRequests().addRequest(warpName, request);
        if (requestSuccess) {

            // Message the player that the request was successful
            plugin.getMessenger().chat(player, "warpMessages.warpRequestAdded", "%warp%", warpName);

            // Remove the tokens from the players inventory
            ItemStack requiredTokens = warpToken.clone();
            ItemStackUtils.removeItem(player, requiredTokens, plugin.getSettings().getCreateWarpCost());

        } else {
            plugin.getMessenger().chat(player, "warpMessages.warpAlreadyExists", "%warp%", warpName);
        }

        return new FeaturedWarpsGUI(null, plugin, player);

    }
}
