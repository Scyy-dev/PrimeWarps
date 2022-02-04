package me.scyphers.minecraft.primewarps.gui;

import me.Scyy.PrimeWarps.Util.ItemBuilder;
import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.scycore.gui.GUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ChooseCategoryGUI extends PagedListGUI<String> {

    private final PrimeWarps plugin;

    private final Warp warp;

    public ChooseCategoryGUI(@Nullable GUI<?> lastGUI, @NotNull PrimeWarps plugin, @NotNull Player player, Warp warp) {
        super(lastGUI, plugin, player, "&5Choose Category", 54,
                new ArrayList<>(plugin.getSettings().getCategories()),
                2, 7, 47, 51);
        this.plugin = plugin;
        this.warp = warp;
    }

    @Override
    public void draw() {
        super.draw();
        inventoryItems[49] = new ItemBuilder(Material.BARRIER).name("&5Back to Featured Warps").build();
    }

    @Override
    public @NotNull ItemStack display(String item) {
        Material categoryMaterial = plugin.getSettings().getCategoryMaterial(item);
        ItemBuilder builder = new ItemBuilder(categoryMaterial).name("&6" + item);
        if (warp.getCategory().equals(item)) builder.enchant();
        return builder.build();
    }

    @Override
    public ItemStack displayBlank() {
        return null;
    }

    @Override
    public @NotNull ItemStack previousPageButton(int page) {
        return new ItemBuilder(Material.ARROW).name("&5Page " + page).build();
    }

    @Override
    public @NotNull ItemStack nextPageButton(int page) {
        return new ItemBuilder(Material.ARROW).name("&5Page " + page).build();
    }

    @Override
    public @NotNull GUI<?> handleGenericInteraction(InventoryClickEvent event) {

        int click = event.getRawSlot();
        event.setCancelled(true);

        if (click == 49) {
            return lastGUI;
        }

        // TODO - complete
        // return new WarpManagerGUI();
        return null;
    }

    @Override
    public @NotNull GUI<?> handleItemInteraction(InventoryClickEvent event, String category) {

        event.setCancelled(true);

        // Ignore the click if they have already selected this category
        if (warp.getCategory().equals(category)) return this;

        warp.setCategory(category);
        return this;
    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }
}
