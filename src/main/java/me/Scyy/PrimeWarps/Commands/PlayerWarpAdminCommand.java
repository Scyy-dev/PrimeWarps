package me.Scyy.PrimeWarps.Commands;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Warps.Warp;
import me.Scyy.PrimeWarps.Warps.WarpRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;
import java.util.Locale;

public class PlayerWarpAdminCommand implements TabExecutor {

    private final Plugin plugin;

    private final PlayerMessenger pm;

    public PlayerWarpAdminCommand(Plugin plugin) {
        this.plugin = plugin;
        this.pm = plugin.getCFH().getPlayerMessenger();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length == 0) {
            plugin.splashText(sender);
            return true;
        }

        switch (args[0].toLowerCase(Locale.ENGLISH)) {

            case "remove":
                if (!sender.hasPermission("pwarp.admin.remove")) {
                    // TODO - send no permission message
                    return true;
                }
                removeWarpSubcommand(sender, args);
                return true;
            case "request":
                if (!sender.hasPermission("pwarp.admin.request")) {
                    // TODO - send no permission message
                    return true;
                }
                requestSubcommand(sender, args);
                return true;
            case "reload":
                if (!sender.hasPermission("pwarp.admin.reload")) {
                    // TODO - send no permission message
                    return true;
                }
                plugin.reload(sender);
            default:
                // TODO - send invalid command message
                return true;
        }

    }



    private void removeWarpSubcommand(CommandSender sender, String[] args) {

        if (args.length < 2) {
            // TODO - send invalid command length message
            return;
        }

        boolean warpRemoved = plugin.getWarpRegister().removeWarp(args[1]);
        if (warpRemoved) {
            // TODO - send warp removed message
        } else {
            // TODO - send warp not found message
        }
    }

    private void requestSubcommand(CommandSender sender, String[] args) {

        if (args.length < 3) {
            // TODO - send invalid command length message
            return;
        }

        String warpName = args[1];

        WarpRequest request = plugin.getWarpRegister().getWarpRequest(warpName);

        if (request == null) {
            // TODO - send could not find warp request message
            return;
        }

        switch (args[2].toLowerCase(Locale.ENGLISH)) {
            case "reject":
                // TODO - possibly refund materials to user that made the warp?
                boolean requestRemoved = plugin.getWarpRegister().removeWarpRequest(request.getName());
                if (requestRemoved) {
                    // TODO - send warp request rejected message
                } else {
                    // TODO - send warp request not found message
                }
                return;
            case "approve":
                Warp warp = new Warp(request);
                boolean warpAdded = plugin.getWarpRegister().addWarp(warp.getName(), warp);
                if (warpAdded) {
                    // TODO - send warp added message
                } else {
                    // TODO - send warp already exists message
                    plugin.getWarpRegister().removeWarpRequest(request.getName());
                }
                return;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return null;
    }
}
