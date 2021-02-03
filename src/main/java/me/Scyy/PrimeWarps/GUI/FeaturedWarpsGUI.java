package me.Scyy.PrimeWarps.GUI;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.GUI.Type.GUI;
import me.Scyy.PrimeWarps.GUI.Type.InventoryGUI;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.DateUtils;
import me.Scyy.PrimeWarps.Util.ItemBuilder;
import me.Scyy.PrimeWarps.Util.WarpUtils;
import me.Scyy.PrimeWarps.Warps.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

// # # # # # # # # #
// # 1 # 2 # 3 # 4 #
// # # # # # # # # #
// # # M # W # C # #
// R # # # # # # # #
// Number - Featured warp
// W - Warp list
// M - Manage your warps
// R - Warp Requests
// C - Create Warp Request

public class FeaturedWarpsGUI extends InventoryGUI {

    private final List<Warp> featuredWarps;

    public FeaturedWarpsGUI(GUI<?> lastGUI, Plugin plugin, Player player) {
        super(lastGUI, plugin, player,"&5Featured Warps", 45);

        this.featuredWarps = selectFeaturedWarps(plugin);

        fill();

        // Create the display items
        inventoryItems[10] = createWarpItem(featuredWarps, 0);
        inventoryItems[12] = createWarpItem(featuredWarps, 1);
        inventoryItems[14] = createWarpItem(featuredWarps, 2);
        inventoryItems[16] = createWarpItem(featuredWarps, 3);

        // Create the manage warps button
        inventoryItems[29] = new ItemBuilder(Material.COMPARATOR).name("&5Your Warps").build();

        // Create the Warp List button
        inventoryItems[31] = new ItemBuilder(Material.BOOK).name("&5Warp List").build();

        // Create Warp button
        inventoryItems[33] = new ItemBuilder(Material.ENDER_PEARL).name("&5Create Warp")
                .lore("&8Drop a named item here to create")
                .lore("&8a warp with the name of the item!").build();

        // Add the Requests button if the user has the permission for it
        if (player.hasPermission("pwarp.admin.requests")) {
            inventoryItems[36] = new ItemBuilder(Material.PAPER).name("&5Warp Requests").build();
        }

        List<String> helpLore = plugin.getCFH().getPlayerMessenger().getListMsg("helpMessages.featuredPageHelpLore");

        // Help info item
        inventoryItems[44] = new ItemBuilder(Material.NETHER_STAR).name("&5Need help?")
                .lore(helpLore).build();

    }

    @Override
    public @NotNull GUI<?> handleInteraction(InventoryClickEvent event) {
        List<Integer> warpSlots = Arrays.asList(10, 12, 14, 16);
        int rawSlot = event.getRawSlot();

        // Check if the user is trying to create a warp
        if (rawSlot == 33 && inventoryItems[33].getType() == Material.ENDER_PEARL) {

            event.setCancelled(true);

            // Check if the player can create a warp in the world
            if (!plugin.getCFH().getSettings().getWorlds().contains(player.getWorld().getName())) {
                plugin.getCFH().getPlayerMessenger().msg(player, "errorMessages.illegalWorldForWarp");
                return new FeaturedWarpsGUI(this, plugin, player);
            }

            // Check if the world has a registered sign GUI - log an error that there is not one available
            if (!plugin.getSignManager().isValidWorld(player.getWorld().getName())) {
                plugin.getCFH().getPlayerMessenger().msg(player, "errorMessages.signNotFound");
                return new FeaturedWarpsGUI(this, plugin, player);
            }

            // Check if the sign is available
            if (!plugin.getSignManager().getSign(player.getWorld().getName()).isAvailable()) {
                plugin.getCFH().getPlayerMessenger().msg(player, "errorMessages.signNotAvailable");
                return new FeaturedWarpsGUI(this, plugin, player);
            }

            // Sign is valid and available - open it
            return new CreateWarpGUI(this, plugin, player);

        }

        // Check if the warp chosen is a valid featured warp
        if (warpSlots.contains(rawSlot) && inventoryItems[rawSlot].getType() == Material.PLAYER_HEAD) {

            event.setCancelled(true);

            int featuredSlot = (rawSlot - 10) / 2;

            Warp warp = featuredWarps.get(featuredSlot);

            if (warp.isInactive()) {

                plugin.getCFH().getPlayerMessenger().msg(event.getWhoClicked(), "warpMessages.warpInactive",
                        "%warp%", warp.getName());

                return new FeaturedWarpsGUI(this, plugin, player);

            }

            // Warp the player
            WarpUtils.warp((Player) event.getWhoClicked(), plugin, featuredWarps.get(featuredSlot));

            // Minimise inventory interactions
            return new UninteractableGUI(this);
        }

        // Manage Warps Button
        if (rawSlot == 29 && inventoryItems[29].getType() == Material.COMPARATOR) {

            event.setCancelled(true);

            return new PlayerWarpListGUI(this, plugin, player.getUniqueId(), player, 0);

        }

        // Warp List Button
        if (rawSlot == 31 && inventoryItems[31].getType() == Material.BOOK) {

            event.setCancelled(true);

            return new WarpListGUI(this, plugin, 0, player);

        }

        // Warp Requests button
        if (rawSlot == 36 && player.hasPermission("pwarp.admin.requests")) {

            event.setCancelled(true);

            return new WarpRequestGUI(this, plugin, player, 0);

        }

        // Help button
        if (rawSlot == 44 && inventoryItems[44].getType() == Material.NETHER_STAR) {

            event.setCancelled(true);

            plugin.getCFH().getPlayerMessenger().msg(player, "helpMessages.helpClickMessage");

            this.close = true;

            return new UninteractableGUI(this);

        }

        event.setCancelled(true);

        return new FeaturedWarpsGUI(this, plugin, player);
    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }

    private ItemStack createWarpItem(List<Warp> warps, int index) {
        PlayerMessenger pm = plugin.getCFH().getPlayerMessenger();
        if (index >= warps.size()) return new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(" ").build();
        Warp warp = warps.get(index);
        String warpName = pm.getMsg("warpName", "%warp%", warp.getName());
        String playerName = Bukkit.getOfflinePlayer(warp.getOwner()).getName();
        return new ItemBuilder(Material.PLAYER_HEAD).skull(plugin, warp.getOwner())
                .name(warpName)
                .lore("&8Owner: &7" + playerName)
                .lore("&8Category: &7" + warp.getCategory())
                .lore("&8Date Created: &7" + DateUtils.format(warp.getDateCreated()))
                .lore("")
                .lore("&aLeft click to visit!").build();
    }

    private List<Warp> selectFeaturedWarps(Plugin plugin) {

        // Sort the warps based on a unique hits, time alive and owner last seen

        List<Warp> uniqueHitWarps = plugin.getWarpRegister().getWarps().values().stream()
                .sorted(Comparator.comparingInt(Warp::getRanking).reversed())
                .collect(Collectors.toList());

        // Only allow 1 player to have a warp on the featured warp list
        List<Warp> finalWarps = new ArrayList<>();
        List<UUID> existingUUIDs = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (uniqueHitWarps.size() > i) {
                Warp warp = uniqueHitWarps.get(i);
                if (!existingUUIDs.contains(warp.getOwner()) && !warp.isInactive()) {
                    finalWarps.add(warp);
                    existingUUIDs.add(warp.getOwner());
                }
            }
        }

        return finalWarps;
    }
}
