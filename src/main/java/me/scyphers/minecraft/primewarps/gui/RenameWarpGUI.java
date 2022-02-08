package me.scyphers.minecraft.primewarps.gui;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.gui.sign.SignGUI;
import me.scyphers.minecraft.primewarps.util.ItemStackUtils;
import me.scyphers.minecraft.primewarps.util.WarpUtil;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.scycore.gui.GUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class RenameWarpGUI extends SignGUI {

    private static final PlainTextComponentSerializer plainText = PlainTextComponentSerializer.plainText();

    private final Warp warp;

    public RenameWarpGUI(GUI<?> lastGUI, PrimeWarps plugin, Player player, Warp warp) {
        super(lastGUI, plugin, player, new String[]{
                "",
                "^^^^^^^^^^^^^",
                "please enter",
                "warp name"
        });
        this.warp = warp;
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
            return new WarpManagerGUI(null, plugin, player, warp);
        }

        // Check if the warp already exists
        if (plugin.getWarps().warpExists(warpName)) {
            plugin.getMessenger().chat(player, "warpMessages.warpAlreadyExists", "%warp%", warpName);
            return new WarpManagerGUI(null, plugin, player, warp);
        }
        if (plugin.getWarpRequests().warpRequestExists(warpName)) {
            plugin.getMessenger().chat(player, "warpMessages.warpRequestAlreadyExists", "%warp%", warpName);
            return new WarpManagerGUI(null, plugin, player, warp);
        }

        // Check if the player has the required items
        int renameWarpCost = plugin.getSettings().getRenameWarpCost();
        ItemStack warpToken = plugin.getFileManager().getMiscDataFile().getWarpToken();
        if (!player.getInventory().containsAtLeast(warpToken, renameWarpCost)) {
            plugin.getMessenger().chat(player, "warpMessages.notEnoughWarpShards", "%warp%", warpName);
            return new WarpManagerGUI(null, plugin, player, warp);
        }

        // Only remove items if the player doesn't have management perms
        if (!player.hasPermission("primewarps.warps.manage")) {
            ItemStackUtils.removeItem(player, warpToken, renameWarpCost);
        }

        // Update the warp name
        Warp newWarp = new Warp(warpName, warp.getIslandUUID(), warp.getLocation(), warp.getCategory(), warp.getDateCreated(),
                warp.getLastSeen(), warp.isInactive(), warp.getUniqueVisitors(), warp.getWeeklyVisitors());

        // Remove the old warp and add the new warp
        plugin.getWarps().removeWarp(warp.getName());
        plugin.getWarps().addWarp(warpName, newWarp);

        // Let the player know the warp was renamed successfully
        plugin.getMessenger().chat(player, "warpMessages.warpRenamed", "%warp%", warpName);

        return new WarpManagerGUI(this, plugin, player, warp);

    }

}
