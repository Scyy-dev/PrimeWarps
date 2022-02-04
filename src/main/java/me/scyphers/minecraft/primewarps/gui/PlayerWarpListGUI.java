package me.scyphers.minecraft.primewarps.gui;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.scycore.api.HeadMetaProvider;
import me.scyphers.scycore.api.ItemBuilder;
import me.scyphers.scycore.gui.GUI;
import me.scyphers.scycore.gui.PagedListGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerWarpListGUI extends PagedListGUI<Warp> {

    private final PrimeWarps plugin;

    private final ItemMeta islandOwnerSkullMeta;

    public PlayerWarpListGUI(@Nullable GUI<?> lastGUI, @NotNull PrimeWarps plugin, @NotNull Player player, UUID islandUUID) {
        super(lastGUI, plugin, player, "&5Island Warps", 54,
                plugin.getWarps().getIslandWarps(islandUUID), 2, 7, 38, 42);
        this.plugin = plugin;

        UUID islandOwnerUUID = plugin.getSkyblockManager().getIslandOwner(islandUUID);
        this.islandOwnerSkullMeta = HeadMetaProvider.getMeta(plugin, islandOwnerUUID);
    }

    @Override
    public @NotNull ItemStack display(Warp warp) {
        Component warpName = plugin.getMessenger().get("warpName", "%warp%", warp.getName());
        return new ItemBuilder(Material.PLAYER_HEAD).meta(islandOwnerSkullMeta).name(warpName).build();
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

        if (event.getRawSlot() == 49) {
            return new FeaturedWarpsGUI(this, plugin, player);
        } else {
            return this;
        }

    }

    @Override
    public @NotNull GUI<?> handleItemInteraction(InventoryClickEvent event, Warp warp) {
        event.setCancelled(true);
        return new WarpManagerGUI(this, plugin, player, warp);
    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }

}
