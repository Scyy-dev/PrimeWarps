package me.Scyy.PrimeWarps.Util.Prompts;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.GUI.FeaturedWarpsGUI;
import me.Scyy.PrimeWarps.GUI.InventoryGUI;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemStackUtils;
import me.Scyy.PrimeWarps.Warps.Warp;
import me.Scyy.PrimeWarps.Warps.WarpRequest;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateWarpPrompt extends StringPrompt {

    private final Plugin plugin;

    public CreateWarpPrompt(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return plugin.getCFH().getPlayerMessenger().getMsg("otherMessages.createWarpPrompt");
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String answer) {
        PlayerMessenger pm = plugin.getCFH().getPlayerMessenger();

        if (!(conversationContext.getForWhom() instanceof Player)) {
            plugin.getLogger().warning("Error creating warp for warp: " + answer);
            conversationContext.getForWhom().sendRawMessage("Error receiving your new warp name! Please report this bug!");
            return null;
        }

        if (answer.equals("CANCEL")) {
            conversationContext.getForWhom().sendRawMessage(pm.getMsg("otherMessages.warpRequestPromptCancelled"));
            return null;
        }

        // Verify the warp doesn't exist yet
        Warp existingWarp = plugin.getWarpRegister().getWarp(answer);
        if (existingWarp != null) {
            conversationContext.getForWhom().sendRawMessage(pm.getMsg("warpMessages.warpAlreadyExists", "%warp%", answer));
            return this;
        }
        WarpRequest existingRequest = plugin.getWarpRegister().getWarpRequest(answer);
        if (existingRequest != null) {
            conversationContext.getForWhom().sendRawMessage(pm.getMsg("warpMessages.warpRequestAlreadyExists", "%warp%", answer));
            return this;
        }

        Player player = (Player) conversationContext.getForWhom();

        ItemStack warpShard = plugin.getCFH().getMiscDataStorage().getWarpToken();
        int cost = plugin.getCFH().getSettings().getCreateWarpCost();

        if (!player.getInventory().containsAtLeast(warpShard, cost)) {
            conversationContext.getForWhom().sendRawMessage(pm.getMsg("warpMessages.notEnoughWarpShards", "%warp%", answer));
            return null;
        }

        // Remove the warp shards
        ItemStackUtils.removeItem(player, warpShard, cost);

        // Create the new warp
        WarpRequest warpRequest = new WarpRequest(answer, player.getUniqueId(), player.getLocation());

        // Add the new warp
        plugin.getWarpRegister().addWarpRequest(answer, warpRequest);

        // Let the player know the warp request was created
        conversationContext.getForWhom().sendRawMessage(pm.getMsg("warpMessages.warpRequestAdded", "%warp%", warpRequest.getName()));

        return null;
    }
}
