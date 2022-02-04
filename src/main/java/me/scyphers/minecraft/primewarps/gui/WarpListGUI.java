package me.scyphers.minecraft.primewarps.gui;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.util.DateTimeUtil;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.scycore.api.HeadMetaProvider;
import me.scyphers.scycore.api.ItemBuilder;
import me.scyphers.scycore.gui.GUI;
import me.scyphers.scycore.gui.PagedListGUI;
import me.scyphers.scycore.gui.UninteractableGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public class WarpListGUI extends PagedListGUI<Warp> {

    private final PrimeWarps plugin;

    public WarpListGUI(@Nullable GUI<?> lastGUI, @NotNull PrimeWarps plugin, @NotNull Player player) {
        super(lastGUI, plugin, player, "&5Warps", 54, new ArrayList<>(plugin.getWarps().getAllWarps()), 4, 7, 47, 51);
        this.plugin = plugin;
    }

    @Override
    public @NotNull ItemStack display(Warp warp) {

        UUID islandOwnerUUID = plugin.getSkyblockManager().getIslandOwner(warp.getIslandUUID());
        ItemMeta skullMeta = HeadMetaProvider.getMeta(plugin, islandOwnerUUID);

        String islandOwnerName = plugin.getServer().getOfflinePlayer(islandOwnerUUID).getName();

        return new ItemBuilder(skullMeta, Material.PLAYER_HEAD).name(warp.getName())
                .lore("&8Owner: &7" + islandOwnerName)
                .lore("&8Category: &7" + warp.getCategory())
                .lore("&8Date Created: &7" + DateTimeUtil.format(warp.getDateCreated()))
                .lore("")
                .lore("&aLeft click to visit!").build();
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
        }

        // TODO - review sorting process, see trello

        return this;
    }

    @Override
    public @NotNull GUI<?> handleItemInteraction(InventoryClickEvent inventoryClickEvent, Warp warp) {

        // TODO - review warping process

        if (event.getClick() == ClickType.SHIFT_RIGHT && player.hasPermission("pwarp.admin.manage")) {

            return new WarpManagerGUI(this, plugin, player, warp);

        } else if (event.getClick() == ClickType.SHIFT_LEFT && player.hasPermission("pwarp.admin.manage")) {

            Bukkit.getScheduler().runTask(plugin, () -> player.teleport(warp.getLocation()));

            return new UninteractableGUI(this);

        } else {

            if (warp.isInactive()) {
                plugin.getCFH().getPlayerMessenger().msg(player, "warpMessages.warpInactive", "%warp%", warp.getName());
                return new me.Scyy.PrimeWarps.GUI.WarpListGUI(this, plugin, page, player);
            }

            // Mark the inventory to close
            this.close = true;

            // Warp the player
            WarpUtils.warp((Player) event.getWhoClicked(), plugin, warp);

            // To minimise chance of interacting while warping, return the GUI in the state it is in
            return new UninteractableGUI(this);

        }

    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }
}
