package me.Scyy.PrimeWarps.GUI;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemBuilder;
import me.Scyy.PrimeWarps.Util.Prompts.CreateWarpPrompt;
import me.Scyy.PrimeWarps.Util.SkullMetaProvider;
import me.Scyy.PrimeWarps.Util.WarpUtils;
import me.Scyy.PrimeWarps.Warps.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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

    public FeaturedWarpsGUI(InventoryGUI lastGUI, Plugin plugin, Player player) {
        super(lastGUI, "&8Featured Warps", plugin, 45, player);

        // Sort the warps based on number of unique visitors
        this.featuredWarps = plugin.getWarpRegister().getWarps().values().stream()
                .sorted(Comparator.comparingInt(Warp::getUniqueVisitorCount).reversed())
                .collect(Collectors.toList());

        // Create the display items
        inventoryItems[10] = createWarpItem(featuredWarps, 0);
        inventoryItems[12] = createWarpItem(featuredWarps, 1);
        inventoryItems[14] = createWarpItem(featuredWarps, 2);
        inventoryItems[16] = createWarpItem(featuredWarps, 3);

        // Create the manage warps button
        inventoryItems[29] = new ItemBuilder(Material.COMPARATOR).name("&6Your Warps").build();

        // Create the Warp List button
        inventoryItems[31] = new ItemBuilder(Material.BOOK).name("&6Warp List").build();

        // Create Warp button
        inventoryItems[33] = new ItemBuilder(Material.ENDER_PEARL).name("&6Create Warp").build();

        // Add the Requests button if the user has the permission for it
        if (player.hasPermission("pwarp.admin.requests")) {
            inventoryItems[36] = new ItemBuilder(Material.PAPER).name("&6Warp Requests").build();
        }

        List<String> helpLore = plugin.getCFH().getPlayerMessenger().getListMsg("helpMessages.featuredPageHelpLore");

        // Help info item
        inventoryItems[44] = new ItemBuilder(Material.NETHER_STAR).name("&6Need help?")
                .lore(helpLore).build();

    }

    @Override
    public InventoryGUI handleClick(InventoryClickEvent event) {

        List<Integer> warpSlots = Arrays.asList(10, 12, 14, 16);
        int rawSlot = event.getRawSlot();

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
            return new UninteractableGUI(this, plugin, player);
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

        // Create Warp Button
        if (rawSlot == 33 && inventoryItems[33].getType() == Material.ENDER_PEARL) {

            event.setCancelled(true);

            // Create the chat prompt to create the warp
            ConversationFactory factory = new ConversationFactory(plugin);
            Conversation conv = factory.withFirstPrompt(new CreateWarpPrompt(plugin)).withLocalEcho(false)
                    .buildConversation(player);
            conv.begin();

            this.close = true;

            return new UninteractableGUI(this, plugin, player);

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

            return new UninteractableGUI(this, plugin, player);

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
        String warpName = warp.isInactive() ? pm.getMsg("inactiveWarpName", "%warp%", warp.getName()) : pm.getMsg("warpName", "%warp%", warp.getName());
        String visitText = warp.isInactive() ? "" : "&8Left click to visit!";
        String playerName = Bukkit.getOfflinePlayer(warp.getOwner()).getName();
        return new ItemBuilder(Material.PLAYER_HEAD).meta(SkullMetaProvider.getMeta(warp.getOwner()))
                .name(warpName)
                .lore("&8Owner: &5" + playerName)
                .lore("&8Category: &7" + warp.getCategory())
                .lore(visitText).build();
    }
}
