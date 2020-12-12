package me.Scyy.PrimeWarps.GUI;

import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemBuilder;
import me.Scyy.PrimeWarps.Util.SkullMetaProvider;
import me.Scyy.PrimeWarps.Util.WarpUtils;
import me.Scyy.PrimeWarps.Warps.Filters.DateCreatedSorter;
import me.Scyy.PrimeWarps.Warps.Warp;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

// # # # # # # # # #
// # 1 # 2 # 3 # 4 #
// # # # # # # # # #
// # # # # W # # # #
// # # # # # # # # #
// Number - Featured warp
// W - Warp list

public class FeaturedWarpsGUI extends InventoryGUI {

    private final List<Warp> featuredWarps;

    public FeaturedWarpsGUI(InventoryGUI lastGUI, Plugin plugin) {
        super(lastGUI, "&7Featured Warps", plugin, 45);

        // Sort the warps based on number of unique visitors
        this.featuredWarps = plugin.getWarpRegister().getWarps().values().stream()
                .sorted(Comparator.comparingInt(Warp::getUniqueVisitorCount).reversed())
                .collect(Collectors.toList());

        // Create the display items
        inventoryItems[10] = createWarpItem(featuredWarps, 0);
        inventoryItems[12] = createWarpItem(featuredWarps, 1);
        inventoryItems[14] = createWarpItem(featuredWarps, 2);
        inventoryItems[16] = createWarpItem(featuredWarps, 3);

        // Create the Warp List item
        inventoryItems[31] = new ItemBuilder(Material.BOOK).name("&6Warps").build();

    }

    @Override
    public InventoryGUI handleClick(InventoryClickEvent event) {

        List<Integer> warpSlots = Arrays.asList(10, 12, 14, 16);
        int rawSlot = event.getRawSlot();

        // Check if the warp chosen is a valid featured warp
        if (warpSlots.contains(rawSlot) && inventoryItems[rawSlot].getType() == Material.PLAYER_HEAD) {

            event.setCancelled(true);

            int featuredSlot = (rawSlot - 10) / 2;

            // Warp the player
            WarpUtils.warp((Player) event.getWhoClicked(), plugin, featuredWarps.get(featuredSlot));

            // Minimise inventory interactions
            return this;
        }

        if (rawSlot == 31 && inventoryItems[31].getType() == Material.BOOK) {

            event.setCancelled(true);

            return new WarpListGUI(this, plugin, 0, new DateCreatedSorter());

        }

        event.setCancelled(true);

        return new FeaturedWarpsGUI(this, plugin);

    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }

    private ItemStack createWarpItem(List<Warp> warps, int index) {
        if (index >= warps.size()) return new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("").build();
        Warp warp = warps.get(index);
        ItemMeta meta = SkullMetaProvider.getMeta(warp.getOwner());
        meta.setDisplayName(plugin.getCFH().getSettings().getWarpMessage(warp.getName()));
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        skull.setItemMeta(meta);
        return skull;
    }
}
