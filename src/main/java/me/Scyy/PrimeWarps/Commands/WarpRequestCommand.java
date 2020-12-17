package me.Scyy.PrimeWarps.Commands;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemStackUtils;
import me.Scyy.PrimeWarps.Warps.WarpRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class WarpRequestCommand implements TabExecutor {

    private final Plugin plugin;

    private final PlayerMessenger pm;

    public WarpRequestCommand(Plugin plugin) {
        this.plugin = plugin;
        this.pm = plugin.getCFH().getPlayerMessenger();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length == 0) {
            plugin.splashText(sender);
            return true;
        }

        if (!sender.hasPermission("pwarp.warp")) {
            pm.msg(sender, "errorMessages.noPermission");
            return true;
        }

        if (!(sender instanceof Player)) {
            pm.msg(sender, "errorMessages.mustBePlayer");
            return true;
        }

        Player player = (Player) sender;

        // Check if the player has the required items
        ItemStack warpToken = plugin.getCFH().getMiscDataStorage().getWarpToken();
        if (!player.getInventory().containsAtLeast(warpToken, plugin.getCFH().getSettings().getCreateWarpCost())) {
            pm.msg(sender, "warpMessages.notEnoughWarpShards", "%warp%", args[0]);
            return true;
        }

        // Check if the warp already exists
        if (plugin.getWarpRegister().warpExists(args[0])) {
            pm.msg(sender, "warpMessages.warpAlreadyExists", "%warp%", args[0]);
            return true;
        }
        if (plugin.getWarpRegister().warpRequestExists(args[0])) {
            pm.msg(sender, "warpMessages.warpRequestAlreadyExists", "%warp%", args[0]);
            return true;
        }

        // Create the Warp Request
        WarpRequest request = new WarpRequest(args[0], player.getUniqueId(), player.getLocation());
        boolean requestSuccess = plugin.getWarpRegister().addWarpRequest(args[0], request);
        if (requestSuccess) {

            // Message the player that the request was successful
            pm.msg(sender, "warpMessages.warpRequestAdded", "%warp%", args[0]);

            // Remove the tokens from the players inventory
            ItemStack requiredTokens = warpToken.clone();
            ItemStackUtils.removeItem(player, requiredTokens, plugin.getCFH().getSettings().getCreateWarpCost());

        } else {
            pm.msg(sender, "warpMessages.warpAlreadyExists", "%warp%", args[0]);
        }
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
