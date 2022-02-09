package me.scyphers.minecraft.primewarps.gui;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.util.DateTimeUtil;
import me.scyphers.minecraft.primewarps.util.WarpUtil;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.scycore.api.HeadMetaProvider;
import me.scyphers.scycore.api.ItemBuilder;
import me.scyphers.scycore.gui.GUI;
import me.scyphers.scycore.gui.InventoryGUI;
import me.scyphers.scycore.gui.UninteractableGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FeaturedWarpsGUI extends InventoryGUI {

    private final PrimeWarps plugin;

    private final List<Warp> featuredWarps;

    public FeaturedWarpsGUI(@Nullable GUI<?> lastGUI, @NotNull PrimeWarps plugin, @NotNull Player player) {
        super(lastGUI, plugin, player, "&5Featured Warps", 54);
        this.plugin = plugin;

        int ownerDowntimeWeighting = plugin.getSettings().getOwnerDowntimeWeighting();
        int warpUptimeWeighting = plugin.getSettings().getWarpUptimeWeighting();
        int uniqueHitsWeighting = plugin.getSettings().getUniqueHitsWeighting();
        int visitorAverageWeighting = plugin.getSettings().getWeeklyVisitorAverageWeighting();

        this.featuredWarps = plugin.getWarps().getAllWarps().stream()
                .filter(Warp::isActive)
                .filter(distinctKey(Warp::getIslandUUID))
                .sorted(Comparator.comparingInt(value -> value.getRanking(uniqueHitsWeighting, visitorAverageWeighting, warpUptimeWeighting, ownerDowntimeWeighting)))
                .collect(Collectors.toList());

    }

    @Override
    public void draw() {
        super.draw();

        fill();

        // Create the display items
        for (int i = 0; i < 4; i++) {
            ItemStack warpItem = featuredWarps.size() > i ? this.createWarpItem(featuredWarps.get(i)) : this.createWarpItem(null);
            inventoryItems[10 + i*2] = warpItem;
        }

        // Create the manage warps button
        inventoryItems[29] = new ItemBuilder(Material.COMPARATOR).name("&5Your Warps").build();

        // Create the Warp List button
        inventoryItems[31] = new ItemBuilder(Material.BOOK).name("&5Warp List").build();

        // Create Warp button
        inventoryItems[33] = new ItemBuilder(Material.ENDER_PEARL).name("&5Create Warp")
                .lore("&8Click here to create a warp!")
                .lore("&8Enter the name of your warp on the sign").build();

        // Add the Requests button if the user has the permission for it
        if (player.hasPermission("pwarp.admin.requests")) {
            inventoryItems[36] = new ItemBuilder(Material.PAPER).name("&5Warp Requests").build();
        }

        List<String> helpLore = plugin.getMessenger().getRawList("helpMessages.featuredPageHelpLore");

        // Help info item
        inventoryItems[44] = new ItemBuilder(Material.NETHER_STAR).name("&5Need help?")
                .lore(helpLore).build();

    }

    @Override
    public @NotNull GUI<?> handleInteraction(InventoryClickEvent event) {

        event.setCancelled(true);

        int click = event.getRawSlot();

        return switch (click) {

            // Featured Warp
            case 10, 12, 14, 16 -> {

                // Prevent IOOB error by checking that the item clicked was a player head
                if (event.getCurrentItem() == null || InventoryGUI.BACKGROUND.getType() == event.getCurrentItem().getType()) {
                    yield this;
                }

                int warpClick = (click - 10) / 2;
                Warp warp = featuredWarps.get(warpClick);

                WarpUtil.warp(player, plugin, warp);

                this.setShouldClose(true);

                yield new UninteractableGUI(this);

            }

            // Warp Manager
            case 29 -> {

                // Ensure the player has an island
                if (!plugin.getSkyblockManager().hasIsland(player.getUniqueId())) {
                    plugin.getMessenger().chat(player, "errorMessages.needIsland");
                    yield this;
                }


                UUID islandUUID = plugin.getSkyblockManager().getIslandUUID(player.getUniqueId());
                yield new PlayerWarpListGUI(this, plugin, player, islandUUID);
            }

            // Warp List
            case 31 -> new WarpListGUI(this, plugin, player, "");

            // Warp Request
            case 33 -> {

                // Ensure the player has an island
                if (!plugin.getSkyblockManager().hasIsland(player.getUniqueId())) {
                    plugin.getMessenger().chat(player, "errorMessages.needIsland");
                    yield this;
                }

                yield new CreateWarpGUI(this, plugin, player);
            }

            // Warp Request Management
            case 36 -> {
                if (!player.hasPermission("primewarps.warps.requests")) yield this;
                else yield new WarpRequestGUI(this, plugin, player);
            }

            // Everything else
            default -> this;

        };
    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }

    private ItemStack createWarpItem(@Nullable Warp warp) {
        if (warp == null) return InventoryGUI.BACKGROUND;
        String warpName = plugin.getMessenger().getRaw("warpName", "%warp%", warp.getName());

        UUID islandOwnerUUID = plugin.getSkyblockManager().getIslandOwner(warp.getIslandUUID());
        String ownerName = plugin.getServer().getOfflinePlayer(islandOwnerUUID).getName();

        ItemMeta headMeta = HeadMetaProvider.getMeta(plugin, islandOwnerUUID);

        return new ItemBuilder(headMeta, Material.PLAYER_HEAD)
                .name(warpName)
                .lore("&8Island Owner: &7" + ownerName)
                .lore("&8Category: " + (warp.getCategory().equals("") ? "&cN/A" : "&7" + warp.getCategory()))
                .lore("&8Date Created: &7" + DateTimeUtil.format(warp.getDateCreated()))
                .lore("")
                .lore("&aLeft click to visit!").build();
    }

    private static <T> Predicate<T> distinctKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
