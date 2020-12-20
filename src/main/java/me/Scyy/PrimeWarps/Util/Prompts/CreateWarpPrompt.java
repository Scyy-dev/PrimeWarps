package me.Scyy.PrimeWarps.Util.Prompts;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemStackUtils;
import me.Scyy.PrimeWarps.Warps.Warp;
import me.Scyy.PrimeWarps.Warps.WarpRequest;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;

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

        if (answer == null) return this;

        String formatName = answer.toLowerCase(Locale.ENGLISH);

        if (!(conversationContext.getForWhom() instanceof Player)) {
            plugin.getLogger().warning("Error creating warp for warp: " + formatName);
            conversationContext.getForWhom().sendRawMessage("Error receiving your new warp name! Please report this bug!");
            return null;
        }

        if (formatName.equalsIgnoreCase("cancel")) {
            conversationContext.getForWhom().sendRawMessage(pm.getMsg("otherMessages.warpRequestPromptCancelled"));
            return null;
        }

        // Verify the warp doesn't exist yet
        Warp existingWarp = plugin.getWarpRegister().getWarp(formatName);
        if (existingWarp != null) {
            conversationContext.getForWhom().sendRawMessage(pm.getMsg("warpMessages.warpAlreadyExists", "%warp%", formatName));
            return this;
        }
        WarpRequest existingRequest = plugin.getWarpRegister().getWarpRequest(formatName);
        if (existingRequest != null) {
            conversationContext.getForWhom().sendRawMessage(pm.getMsg("warpMessages.warpRequestAlreadyExists", "%warp%", formatName));
            return this;
        }

        Player player = (Player) conversationContext.getForWhom();

        List<String> permittedWorlds = plugin.getCFH().getSettings().getWorlds();
        if (!permittedWorlds.contains(player.getWorld().getName())) {
            conversationContext.getForWhom().sendRawMessage(pm.getMsg("errorMessages.illegalWorldForWarp"));
            return this;
        }

        ItemStack warpShard = plugin.getCFH().getMiscDataStorage().getWarpToken();
        int cost = plugin.getCFH().getSettings().getCreateWarpCost();

        if (!player.getInventory().containsAtLeast(warpShard, cost)) {
            conversationContext.getForWhom().sendRawMessage(pm.getMsg("warpMessages.notEnoughWarpShards", "%warp%", formatName));
            return null;
        }

        // Remove the warp shards
        ItemStackUtils.removeItem(player, warpShard, cost);

        // Create the new warp
        WarpRequest warpRequest = new WarpRequest(formatName, player.getUniqueId(), player.getLocation());

        // Add the new warp
        plugin.getWarpRegister().addWarpRequest(formatName, warpRequest);

        // Let the player know the warp request was created
        conversationContext.getForWhom().sendRawMessage(pm.getMsg("warpMessages.warpRequestAdded", "%warp%", warpRequest.getName()));

        return null;
    }
}
