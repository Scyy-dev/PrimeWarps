package me.Scyy.PrimeWarps.Commands;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.GUI.FeaturedWarpsGUI;
import me.Scyy.PrimeWarps.GUI.InventoryGUI;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Util.WarpUtils;
import me.Scyy.PrimeWarps.Warps.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerWarpCommand implements TabExecutor {

    private final Plugin plugin;

    private final PlayerMessenger pm;

    public PlayerWarpCommand(Plugin plugin) {
        this.plugin = plugin;
        this.pm = plugin.getCFH().getPlayerMessenger();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length == 0) {

            if (!sender.hasPermission("pwarp.warp.gui")) {
                pm.msg(sender, "errorMessages.noPermission");
                return true;
            }

            if (sender instanceof Player) {
                Player player = (Player) sender;
                InventoryGUI gui = new FeaturedWarpsGUI(null, plugin, player);
                player.openInventory(gui.getInventory());
            } else {
                pm.msg(sender, "errorMessages.mustBePlayer");
            }

            return true;

        }

        // User is trying to command line warp
        if (!sender.hasPermission("pwarp.warp")) {
            pm.msg(sender, "errorMessages.noPermission");
            return true;
        }

        if (!(sender instanceof Player)) {
            pm.msg(sender, "errorMessages.mustBePlayer");
            return true;
        }

        // Get the warp from the provided name
        Warp warp = plugin.getWarpRegister().getWarp(args[0]);

        if (warp == null) {
            pm.msg(sender, "warpMessages.warpNotFound", "%warp%", args[0]);
            return true;
        }

        if (warp.isInactive()) {
            pm.msg(sender, "warpMessages.warpInactive", "%warp%", warp.getName());
            return true;
        }

        // Warp the player
        WarpUtils.warp(null, (Player) sender, plugin, warp);
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        List<String> list = new ArrayList<>();

        if (sender.hasPermission("pwarp.warp")) {
            for (Warp warp : plugin.getWarpRegister().getWarps().values()) {
                list.add(warp.getName());
            }
            return list;
        } else {
            return Collections.emptyList();
        }
    }
}
