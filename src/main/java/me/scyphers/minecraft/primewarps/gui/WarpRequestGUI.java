package me.scyphers.minecraft.primewarps.gui;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.util.DateTimeUtil;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.minecraft.primewarps.warps.WarpRequest;
import me.scyphers.minecraft.primewarps.warps.WarpRequestResponse;
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

import java.util.ArrayList;
import java.util.UUID;

public class WarpRequestGUI extends PagedListGUI<WarpRequest> {

    private final PrimeWarps plugin;

    public WarpRequestGUI(@Nullable GUI<?> lastGUI, @NotNull PrimeWarps plugin, @NotNull Player player) {
        super(lastGUI, plugin, player, "&5Warp Requests", 54, new ArrayList<>(plugin.getWarpRequests().getRequests()),
                4, 7, 46, 52);
        this.plugin = plugin;
    }

    @Override
    public void draw() {
        super.draw();
        inventoryItems[49] = new ItemBuilder(Material.BARRIER).name("&5Back to Featured Warps").build();
    }

    @Override
    public @NotNull ItemStack display(WarpRequest warpRequest) {
        String formattedDate = DateTimeUtil.format(warpRequest.dateCreated(), true);

        UUID islandOwnerUUID = plugin.getSkyblockManager().getIslandOwner(warpRequest.islandUUID());
        ItemMeta skullMeta = HeadMetaProvider.getMeta(plugin, islandOwnerUUID);

        String ownerName = plugin.getServer().getOfflinePlayer(islandOwnerUUID).getName();

        return new ItemBuilder(skullMeta, Material.PLAYER_HEAD)
                .lore("&r&8Owner: &7" + ownerName)
                .lore("&r&8Date Created: &7" + formattedDate)
                .lore("&r&7Left click&8 to &eTELEPORT &8to warp")
                .lore("&r&7Shift-Left&8 click to &aAPPROVE &8warp")
                .lore("&r&7Shift-Right&8 click to &cREJECT &8warp")
                .build();
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

        if (event.getRawSlot() == 49) return new FeaturedWarpsGUI(this, plugin, player);

        return this;
    }

    @Override
    public @NotNull GUI<?> handleItemInteraction(InventoryClickEvent event, WarpRequest warpRequest) {

        event.setCancelled(true);

        return switch (event.getClick()) {

            // Force Teleport
            case LEFT, RIGHT, MIDDLE -> {
                this.setShouldClose(true);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> event.getWhoClicked().teleport(warpRequest.location()), 2L);
                plugin.getMessenger().chat(event.getWhoClicked(), "warpMessages.playerWarped", "%warp%", warpRequest.name());
                yield new UninteractableGUI(this);
            }

            // Approve Warp
            case SHIFT_LEFT -> {

                // Approve the warp and remove the request
                plugin.getWarps().addWarp(warpRequest.name(), new Warp(warpRequest));
                plugin.getWarpRequests().removeWarpRequest(warpRequest.name());
                plugin.getMessenger().chat(event.getWhoClicked(), "warpMessages.warpRequestApproved", "%warp%", warpRequest.name());

                // Schedule a response
                WarpRequestResponse response = new WarpRequestResponse(warpRequest.name(), warpRequest.islandUUID(), warpRequest.requester(), true, false);
                plugin.getResponseManager().scheduleResponse(warpRequest.requester(), response);
                // TODO - do we need response handlers? Or is there enough information in the response?
                // plugin.get.getRequestScheduler().scheduleHandler(warpRequest, "approved");
                yield this;
            }

            // Reject Warp
            case SHIFT_RIGHT -> {

                // Reject the warp and remove the request
                plugin.getWarpRequests().removeWarpRequest(warpRequest.name());
                plugin.getMessenger().chat(event.getWhoClicked(), "warpMessages.warpRequestRejected", "%warp%", warpRequest.name());

                // Schedule a response to the rejection
                WarpRequestResponse response = new WarpRequestResponse(warpRequest.name(), warpRequest.islandUUID(), warpRequest.requester(), false, true);
                plugin.getResponseManager().scheduleResponse(warpRequest.requester(), response);
                yield this;
            }

            // All other click types
            default -> this;

        };

    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }
}
