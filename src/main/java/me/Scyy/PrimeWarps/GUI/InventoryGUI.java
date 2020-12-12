package me.Scyy.PrimeWarps.GUI;

import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryGUI implements InventoryHolder {

    /**
     * A reference to the GUI before this one. Null if the GUI system was just opened
     */
    protected InventoryGUI lastGUI;

    /**
     * Main plugin reference
     */
    protected final Plugin plugin;

    /**
     * The array of items in the inventory
     */
    protected ItemStack[] inventoryItems;

    /**
     * The inventory to be displayed to the user
     */
    protected final Inventory inventory;

    /**
     * Flag for if the listener should close the inventory, overrides reopen
     */
    protected boolean close = false;

    public InventoryGUI(InventoryGUI lastGUI, String name, Plugin plugin, int size) {

        this.lastGUI = lastGUI;
        this.plugin = plugin;
        this.inventoryItems = initialiseDefaultPage(size);
        this.inventory = Bukkit.createInventory(this, size, ChatColor.translateAlternateColorCodes('&', name));

    }

    /**
     * Creates a double chest full of nameless black stained glass panes
     * @return the itemstack array with the glass panes
     */
    private ItemStack[] initialiseDefaultPage(int size) {

        ItemStack[] defaultInv = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            defaultInv[i] = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("").build();
        }

        return defaultInv;

    }

    public abstract InventoryGUI handleClick(InventoryClickEvent event);

    // Getters and Setters
    public InventoryGUI getLastGUI() {
        return lastGUI;
    }

    public void setLastGUI(InventoryGUI lastGUI) {
        this.lastGUI = lastGUI;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public ItemStack[] getInventoryItems() {
        return inventoryItems;
    }

    public Inventory getInventory() {

        // Assign the inventory items to the inventory
        inventory.setContents(inventoryItems);

        // Return the inventory
        return inventory;
    }

    public boolean shouldClose() {
        return close;
    }

    public abstract boolean allowPlayerInventoryEdits();

}
