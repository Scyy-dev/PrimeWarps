package me.Scyy.PrimeWarps.GUI.Type;

import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class InventoryGUI implements InventoryHolder, GUI<InventoryClickEvent> {

    /**
     * The GUI that was open before this one
     */
    protected final GUI<?> lastGUI;

    /**
     * The plugin class
     */
    protected final Plugin plugin;

    /**
     * The player viewing this inventory
     */
    protected final Player player;

    /**
     * The array of items in the inventory
     */
    protected ItemStack[] inventoryItems;

    /**
     * The inventory to be displayed to the player
     */
    protected final Inventory inventory;

    /**
     * Name of the GUI, displayed at the top of the inventory
     */
    protected final String name;

    /**
     * Size of the GUI, also found from {@code inventoryItems.length}
     */
    protected final int size;

    /**
     * If the GUI should close on the next tick rather than open a new GUI
     */
    protected boolean close;

    /**
     * @param lastGUI The GUI that was open before this one, or <code>null</code> if opened for the first time
     * @param plugin  The main plugin instance
     * @param player  The player that this GUI is being presented to
     * @param name    Name of the GUI to be displayed
     */
    public InventoryGUI(@Nullable GUI<?> lastGUI, @NotNull Plugin plugin, @NotNull Player player, @NotNull String name, int size) {
        this.lastGUI = lastGUI;
        this.plugin = plugin;
        this.player = player;
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.size = size;
        this.inventory = Bukkit.createInventory(this, size, this.name);
        this.inventoryItems = inventory.getContents();
    }

    @Override
    public abstract @NotNull GUI<?> handleInteraction(InventoryClickEvent event);

    @Override
    public void open(Player player) {
        inventory.setContents(inventoryItems);
        player.openInventory(inventory);
    }

    @Override
    public @Nullable GUI<?> getLastGUI() {
        return lastGUI;
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public static Listener getListener() {
        return new InventoryListener();
    }

    public ItemStack[] getInventoryItems() {
        return inventoryItems;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    protected void fill() {
        this.fill(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build());
    }

    protected void fill(ItemStack item) {
        for (int i = 0; i < size; i++) {
            inventoryItems[i] = item;
        }

    }

    /**
     * A check if the player can perform actions in their inventory
     * @return if the player can perform actions in their inventory
     */
    public abstract boolean allowPlayerInventoryEdits();

    @Override
    public boolean shouldClose() {
        return this.close;
    }

    private static class InventoryListener implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onInventoryClickEvent(InventoryClickEvent event) {
            // Verify if the inventory interacted with was an InventoryGUI
            // If the inventory interacted with is not a valid GUI then we do not handle this event
            if (!(event.getView().getTopInventory().getHolder() instanceof InventoryGUI)) return;
            InventoryGUI oldGUI = (InventoryGUI) event.getView().getTopInventory().getHolder();

            // Check if the inventory allows player inventory edits, and if so, cancel the interaction
            if (!oldGUI.allowPlayerInventoryEdits() && event.getClickedInventory() instanceof PlayerInventory) {
                event.setCancelled(true);
                return;
            }

            // Handle the interact event and open the new inventory
            GUI<?> newGUI = oldGUI.handleInteraction(event);

            // If the new GUI should close instead of trying to handle new interactions
            if (oldGUI.shouldClose()) {
                Bukkit.getScheduler().runTask(oldGUI.plugin, () -> oldGUI.getPlayer().closeInventory());
                return;
            }

            Bukkit.getScheduler().runTask(oldGUI.plugin, () -> newGUI.open(oldGUI.getPlayer()));
        }
    }

}
