package me.Scyy.PrimeWarps.GUI;

import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemBuilder;
import me.Scyy.PrimeWarps.Util.SkullMetaProvider;
import me.Scyy.PrimeWarps.Warps.Filters.WarpSorter;
import me.Scyy.PrimeWarps.Warps.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

// # # # # # # # # #
// # 0 0 0 0 0 0 0 #
// # 0 0 0 0 0 0 0 #
// # 0 0 0 0 0 0 0 #
// # 0 0 0 0 0 0 0 #
// # P # # # # # N #
// P = previous page
// N = next page

public class WarpListGUI extends InventoryGUI {

    // State
    private final List<Warp> filteredWarps;
    private final WarpSorter warpSorter;
    private final int page;

    private static final String GUI_NAME = "&6&lWarps";

    public WarpListGUI(InventoryGUI lastGUI, Plugin plugin, int page, WarpSorter warpSorter) {
        super(lastGUI, GUI_NAME, plugin, 54);

        this.page = page;
        this.warpSorter = warpSorter;

        Map<String, Warp> warps = plugin.getWarpRegister().getWarps();

        // Index of the warp list
        int warpStartIndex = 28 * page;
        int warpEndIndex = 28 * (page + 1);

        // Index of the GUI inventory
        int invIndex = 10;

        // Iterate over the filtered warps
        this.filteredWarps = warpSorter.sort(warps);
        for (int i = warpStartIndex; i < warpEndIndex; i++) {

            // Put the item in the array if it is accessible, otherwise add air
            if (i < filteredWarps.size()) {

                // Get the warp
                Warp warp = filteredWarps.get(i);

                // Get the skull meta for the warp owner
                ItemMeta skullMeta = SkullMetaProvider.getMeta(warp.getOwner());
                skullMeta.setDisplayName(warp.getName());
                ItemStack warpItem = new ItemStack(Material.PLAYER_HEAD);
                warpItem.setItemMeta(skullMeta);
                inventoryItems[invIndex] = warpItem;

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

            inventoryItems[46] = new ItemBuilder(Material.ARROW).name("&6Page " + page).build();

        }

        // determine the page number
        int nextPageNum = page + 2;

        // Add the next pagination arrow
        inventoryItems[52] = new ItemBuilder(Material.ARROW).name("&6Page " + nextPageNum).build();

    }

    @Override
    public InventoryGUI handleClick(InventoryClickEvent event) {

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

            // Mark the inventory to close
            this.close = true;

            // Schedule the player to be teleported a tick after the inventory is closed
            Bukkit.getScheduler().runTaskLater(plugin, () -> warp.teleport((Player) event.getWhoClicked(), plugin.getCFH().getPlayerMessenger()), 2);

            return this;

        }

        // Check if the item clicked was a back page arrow
        if (clickedSlot == 46) {

            // Cancel the event
            event.setCancelled(true);

            // Return a new inventory back a page
            int newPage = page - 1;
            return new WarpListGUI(this, plugin, newPage, warpSorter);

        }

        // Check if the item clicked was a back page arrow
        if (clickedSlot == 52) {

            // Cancel the event
            event.setCancelled(true);

            // Return a new inventory forward a page
            int newPage = page + 1;
            return new WarpListGUI(this, plugin, newPage, warpSorter);

        }

        event.setCancelled(true);

        // User has clicked something that doesn't interact, so return an unchanged GUI
        return new WarpListGUI(this, plugin, page, warpSorter);

    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }
}
