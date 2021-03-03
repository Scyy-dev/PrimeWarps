package me.Scyy.PrimeWarps.GUI;

import me.Scyy.PrimeWarps.GUI.Type.GUI;
import me.Scyy.PrimeWarps.GUI.Type.InventoryGUI;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemBuilder;
import me.Scyy.PrimeWarps.Util.ItemStackUtils;
import me.Scyy.PrimeWarps.Util.SkullMetaProvider;
import me.Scyy.PrimeWarps.Warps.Warp;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// # # # # # # # # #
// # # # # H # # # #
// # # # # # # # # #
// # # M R # I D # #
// # # # # # # # # #
// # # # # B # # # #
// H - Player Head
// M - Move Warp button
// R - Rename Warp button
// I - Activate an inactive warp
// D - Destroy Warp button
// B - PlayerWarpListGUI button

public class WarpManagerGUI extends InventoryGUI {

    private final Warp warp;

    public WarpManagerGUI(GUI<?> lastGUI, Plugin plugin, Player player, Warp warp) {
        super(lastGUI, plugin, player, "&5Warp '" + warp.getName() + "'", 54);

        this.warp = warp;

        fill();

        ItemBuilder headBuilder = new ItemBuilder(Material.PLAYER_HEAD).skull(plugin, warp.getOwner())
                .name("&5" + warp.getName());
        if (player.hasPermission("pwarp.admin.manage")) headBuilder.lore("&8Unique Visits: &6" + warp.getUniqueVisitors().size());

        inventoryItems[13] = headBuilder.build();

        int moveCost = plugin.getCFH().getSettings().getMoveWarpCost();
        inventoryItems[29] = new ItemBuilder(Material.PISTON).name("&5Move Warp")
                .lore("&8Move the warp to where you are standing!")
                .lore("&8Cost: &6" + moveCost + " &8shards").build();

        int renameCost = plugin.getCFH().getSettings().getRenameWarpCost();
        inventoryItems[30] = new ItemBuilder(Material.NAME_TAG).name("&5Rename Warp")
                .lore("&8Rename the warp to a new name!")
                .lore("&8Follow the chat prompt to finish the renaming")
                .lore("&8Cost: &6" + renameCost + " &8shards").build();

        inventoryItems[31] = new ItemBuilder(Material.HOPPER).name("&5Categorise Warp")
                .lore("&8Pick a category for the warp!").build();

        if (warp.isInactive()) {
            int reactivateCost = plugin.getCFH().getSettings().getReactivateWarpCost();
            inventoryItems[32] = new ItemBuilder(Material.GRAY_DYE).name("&5Reactivate Warp")
                    .lore("&8Reactivate the warp!")
                    .lore("&8Cost: &6" + reactivateCost + " &8shards").build();
        } else {
            inventoryItems[32] = new ItemBuilder(Material.LIME_DYE).name("&5Reactivate Warp")
                    .lore("&8Your warp is already active!").build();
        }

        inventoryItems[33] = new ItemBuilder(Material.TNT).name("&cRemove Warp")
                .lore("&r&8WARNING!")
                .lore("&r&8You are &cNOT &8refunded for removing warps!").build();

        if (player.hasPermission("pwarp.admin.manage")) {
            inventoryItems[49] = new ItemBuilder(Material.BARRIER).name("&5Back to Warp List").build();
        } else {
            inventoryItems[49] = new ItemBuilder(Material.BARRIER).name("&5Back to Your Warps").build();
        }

    }

    @Override
    public @NotNull GUI<?> handleInteraction(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof WarpManagerGUI) {
            this.inventoryItems = event.getView().getTopInventory().getContents();
        }

        int rawSlot = event.getRawSlot();

        // Move Warp button
        if (rawSlot == 29 && inventoryItems[29].getType() == Material.PISTON) {

            event.setCancelled(true);

            ItemStack warpToken = plugin.getCFH().getMiscDataStorage().getWarpToken();
            int cost = plugin.getCFH().getSettings().getMoveWarpCost();

            List<String> permittedWorlds = plugin.getCFH().getSettings().getWorlds();
            if (!permittedWorlds.contains(player.getWorld().getName())) {
                plugin.getCFH().getPlayerMessenger().msg(player, "errorMessages.cannotCreateWarpInWorld");
                return new WarpManagerGUI(this, plugin, player, warp);
            }

            if (!player.hasPermission("pwarp.admin.manage")) {

                if (!player.getInventory().containsAtLeast(warpToken, cost)) {
                    plugin.getCFH().getPlayerMessenger().msg(player, "warpMessages.notEnoughWarpShards", "%warp%", warp.getName());
                    return new WarpManagerGUI(this, plugin, player, warp);
                }

                ItemStackUtils.removeItem(player, warpToken, cost);
            }

            // Create a new warp
            Warp newWarp = new Warp(warp.getName(), warp.getOwner(), player.getLocation(), warp.getCategory(), warp.getDateCreated(), warp.getOwnerLastSeen(), warp.isInactive(), warp.getUniqueVisitors(), warp.getWeeklyVisitors());

            // Update the warp
            plugin.getWarpRegister().updateWarp(warp, newWarp);

            // Let the player know the warp was moved
            plugin.getCFH().getPlayerMessenger().msg(player, "warpMessages.warpMoved", "%warp%", newWarp.getName());

            return new WarpManagerGUI(this, plugin, player, warp);

        }

        // Rename Warp button
        if (rawSlot == 30 && inventoryItems[30].getType() == Material.NAME_TAG) {

            event.setCancelled(true);

            // Check if the world has a registered sign GUI - error not logged as players can use the GUI outside of valid worlds
            if (!plugin.getSignManager().isValidWorld(player.getWorld().getName())) {
                plugin.getCFH().getPlayerMessenger().msg(player, "otherMessages.noSignGUIAvailable");
                return new WarpManagerGUI(this, plugin, player, warp);
            }

            // Check if the sign is available
            if (!plugin.getSignManager().getSign(player.getWorld().getName()).isAvailable()) {
                plugin.getCFH().getPlayerMessenger().msg(player, "errorMessages.signNotAvailable");
                return new WarpManagerGUI(this, plugin, player, warp);
            }

            ItemStack warpToken = plugin.getCFH().getMiscDataStorage().getWarpToken();
            int cost = plugin.getCFH().getSettings().getRenameWarpCost();

            if (!player.hasPermission("pwarp.admin.manage")) {
                if (!player.getInventory().containsAtLeast(warpToken, cost)) {
                    plugin.getCFH().getPlayerMessenger().msg(player, "warpMessages.notEnoughWarpShards", "%warp%", warp.getName());
                    return new WarpManagerGUI(this, plugin, player, warp);
                }

                ItemStackUtils.removeItem(player, warpToken, cost);
            }

            return new RenameWarpGUI(this, plugin, player, warp);

        }

        // Change category button
        if (rawSlot == 31 && inventoryItems[31].getType() == Material.HOPPER) {

            event.setCancelled(true);

            return new ChooseCategoryGUI(this, plugin, player, warp, 0);

        }

        // Reactivate Warp button - only fires if the warp is actually inactive
        if (rawSlot == 32 && inventoryItems[32].getType() == Material.GRAY_DYE && warp.isInactive()) {

            event.setCancelled(true);

            ItemStack warpToken = plugin.getCFH().getMiscDataStorage().getWarpToken();
            int cost = plugin.getCFH().getSettings().getReactivateWarpCost();



            if (!player.hasPermission("pwarp.admin.manage")) {
                if (!player.getInventory().containsAtLeast(warpToken, cost)) {
                    plugin.getCFH().getPlayerMessenger().msg(player, "warpMessages.notEnoughWarpShards", "%warp%", warp.getName());
                    return new WarpManagerGUI(this, plugin, player, warp);
                }

                ItemStackUtils.removeItem(player, warpToken, cost);
            }

            // Make the warp active
            warp.setInactive(false);

            return new WarpManagerGUI(this, plugin, player, warp);

        }

        // Delete Warp button
        if (rawSlot == 33 && inventoryItems[33].getType() == Material.TNT) {

            plugin.getWarpRegister().removeWarp(warp.getName());

            event.setCancelled(true);

            if (player.hasPermission("pwarp.admin.manage")) {
                return new WarpListGUI(this, plugin, 0, player);
            } else {
                return new PlayerWarpListGUI(this, plugin, warp.getOwner(), player, 0);
            }

        }

        // Back to warp list button
        if (rawSlot == 49 && inventoryItems[49].getType() == Material.BARRIER) {

            event.setCancelled(true);

            if (player.hasPermission("pwarp.admin.manage")) {
                return new WarpListGUI(this, plugin, 0, player);
            } else {
                return new PlayerWarpListGUI(this, plugin, warp.getOwner(), player, 0);
            }

        }

        // Item that doesn't interact was clicked, do nothing
        event.setCancelled(true);

        return new WarpManagerGUI(this, plugin, player, warp);
    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }
}
