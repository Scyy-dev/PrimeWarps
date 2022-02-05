package me.scyphers.minecraft.primewarps.gui;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.util.DateTimeUtil;
import me.scyphers.minecraft.primewarps.util.WarpUtil;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.scycore.api.HeadMetaProvider;
import me.scyphers.scycore.api.ItemBuilder;
import me.scyphers.scycore.gui.GUI;
import me.scyphers.scycore.gui.PagedListGUI;
import me.scyphers.scycore.gui.UninteractableGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class WarpListGUI extends PagedListGUI<Warp> {

    private final PrimeWarps plugin;

    private String category;

    public WarpListGUI(@Nullable GUI<?> lastGUI, @NotNull PrimeWarps plugin, @NotNull Player player, String category) {
        super(lastGUI, plugin, player, "&5Warps", 54, defaultSortedWarps(plugin.getWarps().getAllWarps()),
                4, 7, 47, 51);
        this.plugin = plugin;
        this.category = category;
    }

    @Override
    public void draw() {
        super.draw();
        inventoryItems[45] = new ItemBuilder(Material.BARRIER).name("&5Back to Featured Warps").build();
        String categoryDisplay = category.equals("") ? "ALL" : category;
        inventoryItems[49] = new ItemBuilder(Material.HOPPER).name("&5Sort Warps")
                .lore("&8Category: &7" + categoryDisplay)
                .lore("")
                .lore("&8Click to switch category!")
                .build();
    }

    @Override
    public @NotNull ItemStack display(Warp warp) {

        UUID islandOwnerUUID = plugin.getSkyblockManager().getIslandOwner(warp.getIslandUUID());
        ItemMeta skullMeta = HeadMetaProvider.getMeta(plugin, islandOwnerUUID);

        String islandOwnerName = plugin.getServer().getOfflinePlayer(islandOwnerUUID).getName();

        ItemBuilder builder = new ItemBuilder(skullMeta, Material.PLAYER_HEAD).name(warp.getName())
                .lore("&8Owner: &7" + islandOwnerName)
                .lore("&8Category: &7" + warp.getCategory())
                .lore("&8Date Created: &7" + DateTimeUtil.format(warp.getDateCreated()))
                .lore("")
                .lore("&aLeft click to visit!");

        if (player.hasPermission("primewarps.admin.manage")) {
            builder.lore("")
                    .lore("&8Shift right click to manage this warp")
                    .lore("&8Shift left click to force TP to this warp");
        }

        return builder.build();

    }

    @Override
    public @Nullable ItemStack displayBlank() {
        return null;
    }

    @Override
    public @NotNull ItemStack previousPageButton(int i) {
        return new ItemBuilder(Material.ARROW).name("&5Page " + i).build();
    }

    @Override
    public @NotNull ItemStack nextPageButton(int i) {
        return new ItemBuilder(Material.ARROW).name("&5Page " + i).build();
    }

    @Override
    public @NotNull GUI<?> handleGenericInteraction(InventoryClickEvent event) {

        event.setCancelled(true);

        int click = event.getRawSlot();

        return switch (click) {

            // Back button
            case 45 -> new FeaturedWarpsGUI(this, plugin, player);

            // Sort button
            case 49 -> {

                // Find the next category in order
                String nextCategory = "";
                boolean foundPrevious = false;
                for (String category : plugin.getSettings().getCategories()) {
                    if (foundPrevious) {
                        nextCategory = category;
                        break;
                    }
                    if (this.category.equals(category)) foundPrevious = true;
                }

                this.setItems(plugin.getWarps().getWarpsByCategory(nextCategory));
                this.category = nextCategory;
                yield this;
            }

            // Everything else
            default -> this;

        };

    }

    @Override
    public @NotNull GUI<?> handleItemInteraction(InventoryClickEvent event, Warp warp) {

        event.setCancelled(true);

        return switch (event.getClick()) {

            // Warp management
            case SHIFT_RIGHT -> {
                if (!player.hasPermission("primewarps.admin.manage")) {
                    yield new WarpManagerGUI(this, plugin, player, warp);
                } else {
                    yield this;
                }
            }

            // Force teleport
            case SHIFT_LEFT -> {
                if (!player.hasPermission("primewarps.admin.manage")) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        player.teleport(warp.getLocation());
                    });
                    yield new UninteractableGUI(this);
                } else {
                    yield this;
                }
            }

            default -> {
                this.setShouldClose(true);
                WarpUtil.warp(player, plugin, warp);
                yield new UninteractableGUI(this);
            }

        };

    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }

    private static List<Warp> defaultSortedWarps(Collection<Warp> rawWarps) {
        return rawWarps.stream().sorted(Comparator.comparingLong(value -> value.getDateCreated().toEpochMilli())).collect(Collectors.toList());
    }

}
