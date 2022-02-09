package me.scyphers.minecraft.primewarps.gui;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.util.ItemStackUtils;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.scycore.api.HeadMetaProvider;
import me.scyphers.scycore.api.ItemBuilder;
import me.scyphers.scycore.gui.GUI;
import me.scyphers.scycore.gui.InventoryGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class WarpManagerGUI extends InventoryGUI {

    private final PrimeWarps plugin;

    private final Warp warp;

    public WarpManagerGUI(@Nullable GUI<?> lastGUI, @NotNull PrimeWarps plugin, @NotNull Player player, Warp warp) {
        super(lastGUI, plugin, player, "&5Warp Management", 54);
        this.plugin = plugin;
        this.warp = warp;
    }

    @Override
    public void draw() {
        super.draw();

        this.fill();

        // TODO - check if issues occur when accessed through management permission and not warp owner

        UUID islandOwnerUUID = plugin.getSkyblockManager().getIslandOwner(warp.getIslandUUID());

        ItemMeta headMeta = HeadMetaProvider.getMeta(plugin, islandOwnerUUID);

        String warpName = plugin.getMessenger().getRaw("warpName", "%warp%", warp.getName());
        ItemStack warpOwnerHead = new ItemBuilder(Material.PLAYER_HEAD).meta(headMeta).name(warpName)
                .lore("&8Unique Visits: &6" + warp.getUniqueVisitors().size()).build();

        inventoryItems[13] = warpOwnerHead;

        int moveCost = plugin.getSettings().getMoveWarpCost();
        inventoryItems[29] = new ItemBuilder(Material.PISTON).name("&5Move Warp")
                .lore("")
                .lore("&8Move the warp to where you are standing!")
                .lore("")
                .lore("&8Cost: &6" + moveCost + " &8shards").build();


        int renameCost = plugin.getSettings().getRenameWarpCost();
        inventoryItems[30] = new ItemBuilder(Material.NAME_TAG).name("&5Rename Warp")
                .lore("")
                .lore("&8Rename the warp to a new name!")
                .lore("&8Follow the chat prompt to finish the renaming")
                .lore("")
                .lore("&8Cost: &6" + renameCost + " &8shards").build();

        inventoryItems[31] = new ItemBuilder(Material.HOPPER).name("&5Categorise Warp")
                .lore("&8Pick a category for the warp!").build();

        if (warp.isInactive()) {
            int reactivateCost = plugin.getSettings().getReactivateWarpCost();
            inventoryItems[32] = new ItemBuilder(Material.GRAY_DYE).name("&5Reactivate Warp")
                    .lore("")
                    .lore("&8Reactivate the warp!")
                    .lore("")
                    .lore("&8Cost: &6" + reactivateCost + " &8shards").build();
        } else {
            inventoryItems[32] = new ItemBuilder(Material.LIME_DYE).name("&5Reactivate Warp")
                    .lore("")
                    .lore("&8Your warp is already active!").build();
        }

        inventoryItems[33] = new ItemBuilder(Material.TNT).name("&cRemove Warp")
                .lore("")
                .lore("&r&7&lWARNING!")
                .lore("")
                .lore("&r&8You are &cNOT &8refunded for removing warps!").build();

        if (player.hasPermission("primewarps.warps.manage")) {
            inventoryItems[49] = new ItemBuilder(Material.BARRIER).name("&5Back to Warp List").build();
        } else {
            inventoryItems[49] = new ItemBuilder(Material.BARRIER).name("&5Back to Your Warps").build();
        }

    }

    @Override
    public @NotNull GUI<?> handleInteraction(InventoryClickEvent event) {

        int click = event.getRawSlot();
        event.setCancelled(true);

        switch (click) {

            // Move Warp
            case 29 -> {

                int moveCost = plugin.getSettings().getMoveWarpCost();
                UUID islandOwner = plugin.getSkyblockManager().getIslandOwner(warp.getIslandUUID());

                // Verify the new location is on the players island
                if (!plugin.getSkyblockManager().isWithinPlayerIsland(islandOwner, player.getLocation())) {
                    plugin.getMessenger().chat(player, "warpMessages.notOnPlayerIsland", "%warp%", warp.getName());
                    return this;
                }

                // Verify the player can afford the cost
                ItemStack warpToken = plugin.getFileManager().getMiscDataFile().getWarpToken();
                if (!player.getInventory().containsAtLeast(warpToken, moveCost) && !player.hasPermission("primewarps.warps.manage")) {
                    plugin.getMessenger().chat(player, "warpMessages.notEnoughWarpShards", "%warp%", warp.getName(), "%amount%", "" + moveCost);
                    return this;
                }

                // Only remove items if the player doesn't have management permissions
                if (!player.hasPermission("primewarps.warps.manage")) {
                    ItemStackUtils.removeItem(player, warpToken, moveCost);
                }

                // Move the warp
                warp.setLocation(player.getLocation());
                return this;
            }

            // Rename Warp
            case 30 -> {

                int renameCost = plugin.getSettings().getRenameWarpCost();
                ItemStack warpToken = plugin.getFileManager().getMiscDataFile().getWarpToken();

                // Verify the player can afford the cost
                if (!player.getInventory().containsAtLeast(warpToken, renameCost) && !player.hasPermission("primewarps.warps.manage")) {
                    plugin.getMessenger().chat(player, "warpMessages.notEnoughWarpShards", "%warp%", warp.getName(), "%amount%", "" + renameCost);
                    return this;
                }

                // Only remove items if the player doesn't have management permissions
                if (!player.hasPermission("primewarps.warps.manage")) {
                    ItemStackUtils.removeItem(player, warpToken, renameCost);
                }

                // Open the rename GUI
                return new RenameWarpGUI(this, plugin, player, warp);

            }

            // Change Category
            case 31 -> {

                return new ChooseCategoryGUI(this, plugin, player, warp);

            }

            // Reactivate Warp
            case 32 -> {

                if (!warp.isInactive()) return this;

                int reactivateCost = plugin.getSettings().getReactivateWarpCost();
                ItemStack warpToken = plugin.getFileManager().getMiscDataFile().getWarpToken();

                // Verify player has required permissions
                if (!plugin.getSkyblockManager().hasAtLeast(player.getUniqueId(), "COOWNER") && !player.hasPermission("primewarps.warps.manage")) {
                    plugin.getMessenger().chat(player, "errorMessages.noIslandPermission", "%role%", "COOWNER");
                    return this;
                }

                // Verify the player can afford the cost
                if (!player.getInventory().containsAtLeast(warpToken, reactivateCost) && !player.hasPermission("primewarps.warps.manage")) {
                    plugin.getMessenger().chat(player, "warpMessages.notEnoughWarpShards", "%warp%", warp.getName(), "%amount%", "" + reactivateCost);
                    return this;
                }

                // Only remove items if the player doesn't have management permissions
                if (!player.hasPermission("primewarps.warps.manage")) {
                    ItemStackUtils.removeItem(player, warpToken, reactivateCost);
                }

                // Set the warp as active
                warp.setInactive(false);

                return this;

            }

            // Delete Warp
            case 33 -> {

                // Verify player has required permissions
                if (!plugin.getSkyblockManager().hasAtLeast(player.getUniqueId(), "COOWNER") && !player.hasPermission("primewarps.warps.manage")) {
                    plugin.getMessenger().chat(player, "errorMessages.noIslandPermission", "%role%", "COOWNER");
                    return this;
                }

                // Delete the warp
                plugin.getWarps().removeWarp(warp.getName());

                if (player.hasPermission("primewarps.warps.manage")) return new WarpListGUI(this, plugin, player, "");
                else {
                    UUID islandUUID = plugin.getSkyblockManager().getIslandUUID(player.getUniqueId());
                    return new PlayerWarpListGUI(this, plugin, player, islandUUID);
                }

            }

            // Back page
            case 49 -> {
                if (player.hasPermission("primewarps.warps.manage")) return new WarpListGUI(this, plugin, player, "");
                else {
                    UUID islandUUID = plugin.getSkyblockManager().getIslandUUID(player.getUniqueId());
                    return new PlayerWarpListGUI(this, plugin, player, islandUUID);
                }
            }

            default -> {
                return this;
            }

        }

    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }

}
