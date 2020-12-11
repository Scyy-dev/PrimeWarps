package me.Scyy.PrimeWarps.Commands;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Warps.Warp;
import org.bukkit.Bukkit;
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

        System.out.println(plugin.getWarpRegister().getWarps().toString());
        System.out.println(plugin.getWarpRegister().getWarpRequests().toString());

        if (args.length == 0) {

            if (!sender.hasPermission("pwarp.warp")) {
                pm.msg(sender, "errorMessages.noPermission", false);
                return true;
            }

            if (sender instanceof Player) {
                Player player = (Player) sender;
                // TODO - open warp GUI
                sender.sendMessage("Opening GUI");
            } else {
                pm.msg(sender, "errorMessages.mustBePlayer", true);
            }

            return true;

        }

        // User is trying to command line warp
        if (!sender.hasPermission("pwarp.warp")) {
            pm.msg(sender, "errorMessages.noPermission", false);
            return true;
        }

        if (!(sender instanceof Player)) {
            pm.msg(sender, "errorMessages.mustBePlayer", true);
            return true;
        }

        // Get the warp from the provided name
        Warp warp = plugin.getWarpRegister().getWarp(args[0]);

        if (warp == null) {
            pm.msg(sender, "warpMessages.warpNotFound", false, "%warp%", args[0]);
            return true;
        }

        // Schedule a teleport
        Bukkit.getScheduler().runTask(plugin, () -> warp.teleport((Player) sender, pm));
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
