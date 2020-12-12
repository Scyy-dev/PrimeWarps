package me.Scyy.PrimeWarps.GUI;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemBuilder;
import me.Scyy.PrimeWarps.Util.SkullMetaProvider;
import me.Scyy.PrimeWarps.Warps.Warp;
import me.Scyy.PrimeWarps.Warps.WarpRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

// # # # # # # # # #
// # 0 0 0 0 0 0 0 #
// # 0 0 0 0 0 0 0 #
// # 0 0 0 0 0 0 0 #
// # 0 0 0 0 0 0 0 #
// # P # # # # # N #
// P = previous page
// N = next page

public class WarpRequestGUI extends InventoryGUI {

    private int page;

    private final List<WarpRequest> requests;

    public WarpRequestGUI(InventoryGUI lastGUI, Plugin plugin, int page) {
        super(lastGUI, "&6Warp Requests", plugin, 54);

        this.page = page;

        Map<String, WarpRequest> warpRequests = plugin.getWarpRegister().getWarpRequests();

        // Index of the warp list
        int warpStartIndex = 28 * page;
        int warpEndIndex = 28 * (page + 1);

        // Index of the GUI inventory
        int invIndex = 10;

        // Iterate over the Warp Requests
        this.requests = new ArrayList<>(warpRequests.values());
        for (int i = warpStartIndex; i < warpEndIndex; i++) {

            // Put the item in the array if it is accessible, otherwise add air
            if (i < requests.size()) {

                // Get the warp
                WarpRequest warp = requests.get(i);

                String warpOwner = Bukkit.getOfflinePlayer(warp.getOwner()).getName();

                // Get the skull meta for the warp owner
                ItemMeta skullMeta = SkullMetaProvider.getMeta(warp.getOwner());
                skullMeta.setDisplayName(plugin.getCFH().getSettings().getWarpMessage(warp.getName()));
                List<String> interaction = Arrays.asList(
                        ChatColor.translateAlternateColorCodes('&', "&r&7Owner: " + warpOwner),
                        ChatColor.translateAlternateColorCodes('&', "&r&7Shift-Left click to APPROVE warp"),
                        ChatColor.translateAlternateColorCodes('&', "&r&7Middle click to Teleport to warp"),
                        ChatColor.translateAlternateColorCodes('&', "&r&7Shift-Right click to REJECT warp"));
                skullMeta.setLore(interaction);
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

        PlayerMessenger pm = plugin.getCFH().getPlayerMessenger();

        if (event.getView().getTopInventory().getHolder() instanceof WarpRequestGUI) {
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

            // Get the warpRequest
            WarpRequest warpRequest = requests.get(warpSlot + 28 * page);

            // Cancel the event
            event.setCancelled(true);

            switch (event.getClick()) {

                // Approve the request
                case SHIFT_LEFT:
                    plugin.getWarpRegister().addWarp(warpRequest.getName(), new Warp(warpRequest));
                    plugin.getWarpRegister().removeWarpRequest(warpRequest.getName());
                    pm.msg(event.getWhoClicked(), "warpMessages.warpRequestApproved", true, "%warp%", warpRequest.getName());
                    // Schedule a handler for the message and refund if needed
                    plugin.getWarpRegister().getRequestScheduler().scheduleHandler(warpRequest, "approved");
                    return new WarpRequestGUI(this, plugin, 0);
                // Teleport the user
                case MIDDLE:
                    this.close = true;
                    Bukkit.getScheduler().runTask(plugin, () -> event.getWhoClicked().teleport(warpRequest.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND));
                    pm.msg(event.getWhoClicked(), "warpMessages.playerWarped", true, "%warp%", warpRequest.getName());
                    return this;
                // Teleport the user
                case SHIFT_RIGHT:
                    plugin.getWarpRegister().removeWarpRequest(warpRequest.getName());
                    pm.msg(event.getWhoClicked(), "warpMessages.warpRequestRejected", true, "%warp%", warpRequest.getName());
                    // Schedule a handler for the message and refund if needed
                    plugin.getWarpRegister().getRequestScheduler().scheduleHandler(warpRequest, "rejected");
                    return new WarpRequestGUI(this, plugin, 0);
            }

        }

        // Check if the item clicked was a back page arrow
        if (clickedSlot == 46 && inventoryItems[46].getType() == Material.ARROW) {

            // Cancel the event
            event.setCancelled(true);

            // Decrement the page
            --page;

            // Return a new inventory back a page
            return new WarpRequestGUI(this, plugin, page);

        }

        // Check if the item clicked was a back page arrow
        if (clickedSlot == 52 && inventoryItems[52].getType() == Material.ARROW) {

            // Cancel the event
            event.setCancelled(true);

            // Increment the page
            ++page;

            // Return a new inventory forward a page
            return new WarpRequestGUI(this, plugin, page);

        }

        event.setCancelled(true);

        // User has clicked something that doesn't interact, so return an unchanged GUI
        return new WarpRequestGUI(this, plugin, page);

    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }
}