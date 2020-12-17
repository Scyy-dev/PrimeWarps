package me.Scyy.PrimeWarps.Util.Prompts;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Warps.Warp;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.time.Instant;

public class RenameWarpPrompt extends StringPrompt {

    private final Warp warp;

    private final Plugin plugin;

    public RenameWarpPrompt(Plugin plugin, Warp warp) {
        this.warp = warp;
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return plugin.getCFH().getPlayerMessenger().getMsg("otherMessages.renameWarpMessage");
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String answer) {

        PlayerMessenger pm = plugin.getCFH().getPlayerMessenger();

        if (!(conversationContext.getForWhom() instanceof Player)) {
            plugin.getLogger().warning("Error reading new warp name for warp " + warp.getName());
            conversationContext.getForWhom().sendRawMessage("Error receiving your new warp name! Please report this bug!");
            return null;
        }

        Warp existingWarp = plugin.getWarpRegister().getWarp(answer);
        if (existingWarp != null) {
            conversationContext.getForWhom().sendRawMessage(pm.getMsg("warpMessages.warpAlreadyExists", "%warp%", answer));
            return this;
        }

        // Create the new warp
        Warp newWarp = new Warp(answer, warp.getOwner(), warp.getLocation(), warp.getCategory(), warp.getDateCreated(), Instant.now(), warp.isInactive(), warp.getUniqueVisitors());

        // Add the new warp
        plugin.getWarpRegister().updateWarp(warp, newWarp);

        // Let the player know the warp was renamed
        conversationContext.getForWhom().sendRawMessage(pm.getMsg("otherMessages.warpRenamed"));

        return null;

    }
}
