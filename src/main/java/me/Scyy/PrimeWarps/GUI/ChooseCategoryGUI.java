package me.Scyy.PrimeWarps.GUI;

import me.Scyy.PrimeWarps.GUI.Type.GUI;
import me.Scyy.PrimeWarps.GUI.Type.InventoryGUI;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemBuilder;
import me.Scyy.PrimeWarps.Warps.Warp;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChooseCategoryGUI extends InventoryGUI {

    private final Warp warp;

    private int page;

    private final List<String> categories;

    public ChooseCategoryGUI(GUI<?> lastGUI, Plugin plugin, Player player, Warp warp, int page) {
        super(lastGUI, plugin, player,"&5Choose Category",54);

        this.warp = warp;
        this.page = page;

        // Default items
        fill();

        // Create the category list
        // Index of the warp list
        int warpStartIndex = 14 * page;
        int warpEndIndex = 14 * (page + 1);

        // Index of the GUI inventory
        int invIndex = 19;

        this.categories = plugin.getCFH().getSettings().getCategories();

        // Iterate over the player warps
        for (int i = warpStartIndex; i < warpEndIndex; i++) {

            // Put the item in the array if it is accessible, otherwise add air
            if (i < categories.size()) {

                // Get the warp
                String category = categories.get(i);

                Material categoryMaterial = plugin.getCFH().getSettings().getCategoryMaterial(category);

                if (warp.getCategory().equals(category)) {
                    inventoryItems[invIndex] = new ItemBuilder(categoryMaterial).name("&5" + category)
                            .lore("&8Click to select this category!").enchant().build();
                } else {
                    inventoryItems[invIndex] = new ItemBuilder(categoryMaterial).name("&5" + category)
                            .lore("&8Click to select this category!").build();
                }

                // Add the item


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

            inventoryItems[47] = new ItemBuilder(Material.ARROW).name("&5Page " + page).build();

        }

        inventoryItems[49] = new ItemBuilder(Material.BARRIER).name("&5Back to '" + warp.getName() + "'").build();

        int nextPage = page + 2;
        inventoryItems[51] = new ItemBuilder(Material.ARROW).name("&5Page " + nextPage).build();

    }

    @Override
    public @NotNull GUI<?> handleInteraction(InventoryClickEvent event) {
        int clickedSlot = event.getRawSlot();

        // slot of the warp in the filtered warp list
        int warpSlot = -1;

        // Check if the item clicked was a piglin item in the inventory
        if (18 < clickedSlot && clickedSlot < 26) warpSlot = clickedSlot - 19;
        else if (27 < clickedSlot && clickedSlot < 35) warpSlot = clickedSlot - 21;

        // Check if the item clicked was a valid warp
        if (warpSlot != -1 && inventoryItems[clickedSlot] != null) {

            // Set the category
            String category = categories.get(warpSlot + 28 * page);
            warp.setCategory(category);

            // Cancel the event
            event.setCancelled(true);

            // Open the Choose Category GUI
            return new ChooseCategoryGUI(this, plugin, player, warp, page);

        }

        if (clickedSlot == 47 && inventoryItems[clickedSlot].getType() == Material.ARROW) {

            // Cancel the event
            event.setCancelled(true);

            // Decrement the page
            --page;

            // Return a new inventory back a page
            return new ChooseCategoryGUI(this, plugin, player, warp, page);

        }

        if (clickedSlot == 49 && inventoryItems[clickedSlot].getType() == Material.BARRIER) {

            event.setCancelled(true);

            return new WarpManagerGUI(this, plugin, player, warp);
        }

        if (clickedSlot == 51 && inventoryItems[clickedSlot].getType() == Material.ARROW) {

            // Cancel the event
            event.setCancelled(true);

            // Increment the page
            ++page;

            // Return a new inventory back a page
            return new ChooseCategoryGUI(this, plugin, player, warp, page);

        }

        event.setCancelled(true);

        return new ChooseCategoryGUI(this, plugin, player, warp, page);
    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }

}
