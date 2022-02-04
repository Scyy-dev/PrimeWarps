package me.scyphers.minecraft.primewarps.gui;

import me.scyphers.scycore.BasePlugin;
import me.scyphers.scycore.gui.GUI;
import me.scyphers.scycore.gui.InventoryGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;

public abstract class PagedListGUI<T> extends InventoryGUI {

    private final ItemStack fillItem;

    private final List<T> items;

    private final int height, width, totalPerPage;

    private int page;

    private final int previousPageSlot, nextPageSlot;

    public PagedListGUI(@Nullable GUI<?> lastGUI, @NotNull BasePlugin plugin, @NotNull Player player,
                        @NotNull String name, int size, List<T> items, int height, int width, int previousPageSlot, int nextPageSlot) {
        this(lastGUI, plugin, player, name, size, items, height, width, BACKGROUND, previousPageSlot, nextPageSlot);
    }

    public PagedListGUI(@Nullable GUI<?> lastGUI, @NotNull BasePlugin plugin, @NotNull Player player,
                        @NotNull String name, int size, List<T> items, @Range(from = 1, to = 4) int height,
                        @Range(from = 1, to = 7) int width, ItemStack fillItem, int previousPageSlot, int nextPageSlot) {
        super(lastGUI, plugin, player, name, size);
        this.items = items;
        this.height = height;
        this.width = width;
        this.totalPerPage = height * width;
        this.fillItem = fillItem;
        this.previousPageSlot = previousPageSlot;
        this.nextPageSlot = nextPageSlot;
        this.draw();
    }

    public void draw() {

        // Fill with all the items
        this.fill(fillItem);

        // Determine the start position of the items from the list
        // cases where the integer is bigger than the list are handling in the loop
        int listIndexStart = totalPerPage * page;

        int columnFromWidth = getColumnFromWidth(width);

        // The index of the items in the inventory, determined based on a simple map from width and height
        int invIndex = 9 * getRowFromHeight(height) + columnFromWidth;

        // Iterate over the current page of items
        for (int i = listIndexStart; i < listIndexStart + totalPerPage; i++) {

            // Check if it is safe to retrieve the item and display it
            if (i < items.size()) {
                T item = items.get(i);
                inventoryItems[invIndex] = this.display(item);
            // Otherwise, display nothing
            } else {
                inventoryItems[invIndex] = this.displayBlank();
            }

            invIndex++;

            // Wrap the inventory index back around if this display row has finished
            if (invIndex - (9 - columnFromWidth) % 9 == 0) invIndex += 2 * columnFromWidth;

        }

        // Display the pagination buttons
        if (page != 0) inventoryItems[this.getSize() - 9] = this.previousPageButton(page);
        inventoryItems[this.getSize() - 1] = this.nextPageButton(page);
    }

    @Override
    public @NotNull GUI<?> handleInteraction(InventoryClickEvent event) {

        int click = event.getRawSlot();

        // Check if the item clicked was an item on the list
        int wrappedClick = getIndexOfItemWithBorder(click, this.getSize(), getRowFromHeight(height) * 2, getColumnFromWidth(width) * 2);
        if (wrappedClick != -1 && inventoryItems[click] != null) {
            int indexedClick = totalPerPage * page + wrappedClick;
            T item = items.get(indexedClick);
            return this.handleItemInteraction(event, item);
        }

        // Check if the item clicked was a pagination button
        if (click == previousPageSlot && page != 0) {
            event.setCancelled(true);
            this.page -= 1;
            this.draw();
            return this;
        }

        if (click == nextPageSlot) {
            event.setCancelled(true);
            this.page += 1;
            this.draw();
            return this;
        }

        return this.handleGenericInteraction(event);
    }

    public abstract @NotNull ItemStack display(T item);

    public abstract @Nullable ItemStack displayBlank();

    public abstract @NotNull ItemStack previousPageButton(int page);

    public abstract @NotNull ItemStack nextPageButton(int page);

    public abstract @NotNull GUI<?> handleGenericInteraction(InventoryClickEvent event);

    public abstract @NotNull GUI<?> handleItemInteraction(InventoryClickEvent event, T item);

    public static int getColumnFromWidth(int width) {
        return switch (width) {
            case 1 -> 4;
            case 2, 3 -> 3;
            case 4, 5 -> 2;
            case 6, 7 -> 1;
            default -> -1;
        };
    }

    public static int getRowFromHeight(int height) {
        return switch (height) {
            case 1, 2 -> 2;
            case 3, 4 -> 1;
            default -> -1;
        };
    }

    public static int getIndexOfItemWithBorder(int inventoryIndex, int inventorySize, int borderRows, int borderColumns) {

        int invRows = inventorySize / 9;
        int invColumns = 9;

        int row = inventoryIndex / 9;
        int column = inventoryIndex % 9;

        if (row < borderRows || row > invRows - borderRows) return -1;
        if (column < borderColumns || column > invColumns - borderColumns) return -1;

        return (row - borderRows) * (invRows - borderColumns * 2) + (column - borderColumns);

    }

}
