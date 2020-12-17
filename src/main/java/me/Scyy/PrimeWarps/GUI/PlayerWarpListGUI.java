package me.Scyy.PrimeWarps.GUI;

import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemBuilder;
import me.Scyy.PrimeWarps.Util.SkullMetaProvider;
import me.Scyy.PrimeWarps.Warps.Warp;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// # # # # # # # # #
// # # # # # # # # #
// # 0 0 0 0 0 0 0 #
// # 0 0 0 0 0 0 0 #
// # # P # # # N # #
// # # # # # # # # #
// P = previous page
// N = next page

public class PlayerWarpListGUI extends InventoryGUI {

    private final List<Warp> playerWarps;

    private final UUID owner;

    private int page;

    public PlayerWarpListGUI(InventoryGUI lastGUI, Plugin plugin, UUID owner, Player player, int page) {
        super(lastGUI, "&8Your Warps", plugin, 54, player);

        this.owner = owner;
        this.page = page;
        this.playerWarps = new ArrayList<>();

        // Get a list of all the players warps
        for (Warp warp : plugin.getWarpRegister().getWarps().values()) {

            if (warp.getOwner().equals(owner)) playerWarps.add(warp);

        }

        // Index of the warp list
        int warpStartIndex = 14 * page;
        int warpEndIndex = 14 * (page + 1);

        // Index of the GUI inventory
        int invIndex = 19;

        // Iterate over the player warps
        for (int i = warpStartIndex; i < warpEndIndex; i++) {

            // Put the item in the array if it is accessible, otherwise add air
            if (i < playerWarps.size()) {

                // Get the warp
                Warp warp = playerWarps.get(i);

                // Add the item
                inventoryItems[invIndex] = new ItemBuilder(Material.PLAYER_HEAD).meta(SkullMetaProvider.getMeta(player.getUniqueId()))
                        .name(plugin.getCFH().getPlayerMessenger().getMsg("warpName", "%warp%", warp.getName())).build();

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

            inventoryItems[38] = new ItemBuilder(Material.ARROW).name("&6Page " + page).build();

        }

        // determine the page number
        int nextPageNum = page + 2;

        // Add the next pagination arrow
        inventoryItems[42] = new ItemBuilder(Material.ARROW).name("&6Page " + nextPageNum).build();

        // Add the back button
        inventoryItems[49] = new ItemBuilder(Material.BARRIER).name("&6Back to Featured Warps").build();

    }

    @Override
    public InventoryGUI handleClick(InventoryClickEvent event) {

        if (event.getView().getTopInventory().getHolder() instanceof PlayerWarpListGUI) {
            this.inventoryItems = event.getView().getTopInventory().getContents();
        }

        int clickedSlot = event.getRawSlot();

        // slot of the warp in the filtered warp list
        int warpSlot = -1;

        // Check if the item clicked was a piglin item in the inventory
        if (18 < clickedSlot && clickedSlot < 26) warpSlot = clickedSlot - 19;
        else if (27 < clickedSlot && clickedSlot < 35) warpSlot = clickedSlot - 21;

        // Check if the item clicked was a valid warp
        if (warpSlot != -1 && inventoryItems[clickedSlot] != null) {

            // Get the warp
            Warp warp = playerWarps.get(warpSlot + 14 * page);

            // Cancel the event
            event.setCancelled(true);

            // Open the warp manager GUI
            return new WarpManagerGUI(this, plugin, player, warp);

        }

        // Check if the item clicked was a back page arrow
        if (clickedSlot == 38 && inventoryItems[38].getType() == Material.ARROW) {

            // Cancel the event
            event.setCancelled(true);

            // Decrement the page
            --page;

            // Return a new inventory back a page
            return new PlayerWarpListGUI(this, plugin, owner, player, page);

        }

        // Check if the item clicked was a forward page arrow
        if (clickedSlot == 42 && inventoryItems[42].getType() == Material.ARROW) {

            // Cancel the event
            event.setCancelled(true);

            // Increment the page
            ++page;

            // Return a new inventory forward a page
            return new PlayerWarpListGUI(this, plugin, owner, player, page);

        }

        // Back to Featured Warps button
        if (clickedSlot == 49 && inventoryItems[49].getType() == Material.BARRIER) {
            event.setCancelled(true);
            return new FeaturedWarpsGUI(this, plugin, player);
        }

        // Return the page as it is on a useless click
        event.setCancelled(true);

        return new PlayerWarpListGUI(this, plugin, owner, player, page);

    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }

}
