package me.Scyy.PrimeWarps.GUI;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.DateUtils;
import me.Scyy.PrimeWarps.Util.ItemBuilder;
import me.Scyy.PrimeWarps.Util.SkullMetaProvider;
import me.Scyy.PrimeWarps.Util.WarpUtils;
import me.Scyy.PrimeWarps.Warps.Warp;
import me.Scyy.PrimeWarps.Warps.WarpSorters.CategorySorter;
import me.Scyy.PrimeWarps.Warps.WarpSorters.DateCreatedSorter;
import me.Scyy.PrimeWarps.Warps.WarpSorters.WarpSorter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// # # # # # # # # #
// # 0 0 0 0 0 0 0 #
// # 0 0 0 0 0 0 0 #
// # 0 0 0 0 0 0 0 #
// # 0 0 0 0 0 0 0 #
// F # P # # # N # S
// P = previous page
// N = next page
// F = Featured warp page
// C = Manage your warps
// S = Warp Sort type

public class WarpListGUI extends InventoryGUI {

    // State
    private final List<Warp> filteredWarps;
    private int page;

    private final WarpSorter warpSorter;

    public WarpListGUI(InventoryGUI lastGUI, Plugin plugin, int page, Player player) {
        this(lastGUI, plugin, page, player, new DateCreatedSorter());
    }

    public WarpListGUI(InventoryGUI lastGUI, Plugin plugin, int page, Player player, WarpSorter warpSorter) {
        super(lastGUI, "&8Warps", plugin, 54, player);

        this.page = page;
        this.warpSorter = warpSorter;

        Map<String, Warp> warps = plugin.getWarpRegister().getWarps();

        // Index of the warp list
        int warpStartIndex = 28 * page;
        int warpEndIndex = 28 * (page + 1);

        // Index of the GUI inventory
        int invIndex = 10;

        PlayerMessenger pm = plugin.getCFH().getPlayerMessenger();

        // Iterate over the filtered warps
        this.filteredWarps = warpSorter.sort(warps);
        for (int i = warpStartIndex; i < warpEndIndex; i++) {

            // Put the item in the array if it is accessible, otherwise add air
            if (i < filteredWarps.size()) {

                // Get the warp
                Warp warp = filteredWarps.get(i);

                String warpName = warp.isInactive() ? pm.getMsg("inactiveWarpName", "%warp%", warp.getName()) : pm.getMsg("warpName", "%warp%", warp.getName());

                String ownerName = Bukkit.getOfflinePlayer(warp.getOwner()).getName();

                String visitText = warp.isInactive() ? "" : "&8Left click to visit!";

                // Check the players permissions for managing warps
                if (player.hasPermission("pwarp.admin.manage")) {
                    inventoryItems[invIndex] = new ItemBuilder(Material.PLAYER_HEAD)
                            .meta(SkullMetaProvider.getMeta(warp.getOwner()))
                            .name(warpName)
                            .lore("&8Owner: &5" + ownerName)
                            .lore("&8Category: &7" + warp.getCategory())
                            .lore("&8Date Created: &7" + DateUtils.format(warp.getDateCreated()))
                            .lore(visitText)
                            .lore("&8Shift right click to manage this warp")
                            .lore("&8Shift left click to force TP to this warp").build();
                } else {
                    inventoryItems[invIndex] = new ItemBuilder(Material.PLAYER_HEAD)
                            .meta(SkullMetaProvider.getMeta(warp.getOwner()))
                            .name(warpName)
                            .lore("&8Owner: &5" + ownerName)
                            .lore("&8Category: &7" + warp.getCategory())
                            .lore("&8Date Created: &7" + DateUtils.format(warp.getDateCreated()))
                            .lore(visitText).build();
                }

            } else {

                // Add an empty value to the inventory array
                inventoryItems[invIndex] = null;

            }

            invIndex++;

            // Check if the inventory slot is one from the edge, and move index to other side
            if ((invIndex - 8) % 9 == 0) invIndex += 2;

        }

        // Check if the page is not 0 and if so add the previous pagination arrow
        if (page != 0) {

            inventoryItems[47] = new ItemBuilder(Material.ARROW).name("&6Page " + page).build();

        }

        // determine the page number
        int nextPageNum = page + 2;

        // Add the next pagination arrow
        inventoryItems[51] = new ItemBuilder(Material.ARROW).name("&6Page " + nextPageNum).build();

        // Add the Back button
        inventoryItems[49] = new ItemBuilder(Material.BARRIER).name("&6Back to Featured Warps").build();

        // Add the sorter button
        inventoryItems[53] = new ItemBuilder(Material.HOPPER).name("&6Sort Warps")
                .lore(this.sorterLore()).build();

    }

    @Override
    public InventoryGUI handleClick(InventoryClickEvent event) {

        if (event.getView().getTopInventory().getHolder() instanceof WarpListGUI) {
            this.inventoryItems = event.getView().getTopInventory().getContents();
        }

        int clickedSlot = event.getRawSlot();

        // slot of the warp in the filtered warp list
        int warpSlot = -1;

        // Check if the item clicked was a piglin item in the inventory
        if (9 < clickedSlot && clickedSlot < 17) warpSlot = clickedSlot - 10;
        else if (18 < clickedSlot && clickedSlot < 26) warpSlot = clickedSlot - 12;
        else if (27 < clickedSlot && clickedSlot < 35) warpSlot = clickedSlot - 14;
        else if (36 < clickedSlot && clickedSlot < 44) warpSlot = clickedSlot - 16;

        // Check if the item clicked was a valid warp
        if (warpSlot != -1 && inventoryItems[clickedSlot] != null) {

            // Get the warp
            Warp warp = filteredWarps.get(warpSlot + 28 * page);

            // Cancel the event
            event.setCancelled(true);

            if (event.getClick() == ClickType.SHIFT_RIGHT && player.hasPermission("pwarp.admin.manage")) {

                return new WarpManagerGUI(this, plugin, player, warp);

            } else if (event.getClick() == ClickType.SHIFT_LEFT && player.hasPermission("pwarp.admin.manage")) {

                Bukkit.getScheduler().runTask(plugin, () -> player.teleport(warp.getLocation()));

                return new UninteractableGUI(this, plugin, player);

            } else {

                if (warp.isInactive()) {
                    plugin.getCFH().getPlayerMessenger().msg(player, "warpMessages.warpInactive", "%warp%", warp.getName());
                    return new WarpListGUI(this, plugin, page, player);
                }

                // Mark the inventory to close
                this.close = true;

                // Warp the player
                WarpUtils.warp(this, (Player) event.getWhoClicked(), plugin, warp);

                // To minimise chance of interacting while warping, return the GUI in the state it is in
                return new UninteractableGUI(this, plugin, player);

            }

        }

        // Check if the item clicked was a back page arrow
        if (clickedSlot == 47 && inventoryItems[47].getType() == Material.ARROW) {

            // Cancel the event
            event.setCancelled(true);

            // Decrement the page
            --page;

            // Return a new inventory back a page
            return new WarpListGUI(this, plugin, page, player, warpSorter);

        }

        // Check if the item clicked was the back button
        if (clickedSlot == 49 && inventoryItems[49].getType() == Material.BARRIER) {

            // Cancel the event
            event.setCancelled(true);

            // Return the Warp Manager GUI
            return new FeaturedWarpsGUI(this, plugin, player);

        }

        // Check if the item clicked was a back page arrow
        if (clickedSlot == 51 && inventoryItems[51].getType() == Material.ARROW) {

            // Cancel the event
            event.setCancelled(true);

            // Increment the page
            ++page;

            // Return a new inventory forward a page
            return new WarpListGUI(this, plugin, page, player, warpSorter);

        }

        if (clickedSlot == 53 && inventoryItems[53].getType() == Material.HOPPER) {

            event.setCancelled(true);

            // Cycle through the sorters
            if (warpSorter instanceof DateCreatedSorter) {

                String category = plugin.getCFH().getSettings().getCategories().get(0);

                return new WarpListGUI(this, plugin, 0, player, new CategorySorter(category));

            } else if (warpSorter instanceof CategorySorter) {

                if (event.getClick() == ClickType.RIGHT) {

                    List<String> categories = plugin.getCFH().getSettings().getCategories();

                    // go to the next category
                    int categoryIndex = categories.indexOf(((CategorySorter) warpSorter).getCategory());
                    int newIndex = categoryIndex >= categories.size() - 1 ? 0 : categoryIndex + 1;

                    String category = categories.get(newIndex);

                    return new WarpListGUI(this, plugin, 0, player, new CategorySorter(category));

                } else {

                    return new WarpListGUI(this, plugin, 0, player, new DateCreatedSorter());

                }

            }

        }

        event.setCancelled(true);

        // User has clicked something that doesn't interact, so return an unchanged GUI
        return new WarpListGUI(this, plugin, page, player, warpSorter);

    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }

    private List<String> sorterLore() {
        List<String> lore = Arrays.asList("  &8By date created", "  &8By category");
        lore.set(warpSorter.listPosition(), warpSorter.getActiveText());
        return lore;
    }

}
