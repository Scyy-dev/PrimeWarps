package me.Scyy.PrimeWarps.Commands;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.ItemBuilder;
import me.Scyy.PrimeWarps.Warps.WarpRequest;
import org.bukkit.Material;
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
            // TODO - send no permission message
            return true;
        }

        if (!(sender instanceof Player)) {
            // TODO - send must be player message
            return true;
        }

        Player player = (Player) sender;

        // Check if the player has the required items
        // ItemStack warpToken = plugin.getCFH().getUserData().getWarpToken();
        ItemStack warpToken = new ItemBuilder(Material.PRISMARINE_SHARD).name("Warp Shard").build();
        if (!player.getInventory().containsAtLeast(warpToken, 10)) {
            // TODO - send couldn't find enough warp tokens message
            return true;
        }

        // Check if the warp already exists
        if (plugin.getWarpRegister().warpExists(args[0]) || plugin.getWarpRegister().warpRequestExists(args[0])) {
            // TODO - send warp already exists messages
            return true;
        }

        // Remove the tokens
        ItemStack requiredTokens = warpToken.clone();
        requiredTokens.setAmount(plugin.getCFH().getSettings().getWarpTokenCount());
        player.getInventory().remove(requiredTokens);

        // Create the Warp Request
        WarpRequest request = new WarpRequest(player.getUniqueId(), args[0], player.getLocation());
        boolean requestSuccess = plugin.getWarpRegister().addWarpRequest(args[0], request);
        if (requestSuccess) {
            // TODO - send warp request added message
        } else {
            // TODO - send warp already exists message
        }

        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
