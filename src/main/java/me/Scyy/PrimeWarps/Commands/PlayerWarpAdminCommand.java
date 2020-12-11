package me.Scyy.PrimeWarps.Commands;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Warps.Warp;
import me.Scyy.PrimeWarps.Warps.WarpRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
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
                    pm.msg(sender, "errorMessages.noPermission", false);
                    return true;
                }
                removeWarpSubcommand(sender, args);
                return true;
            case "request":
                if (!sender.hasPermission("pwarp.admin.request")) {
                    pm.msg(sender, "errorMessages.noPermission", false);
                    return true;
                }
                requestSubcommand(sender, args);
                return true;
            case "reload":
                if (!sender.hasPermission("pwarp.admin.reload")) {
                    pm.msg(sender, "errorMessages.noPermission", false);
                    return true;
                }
                plugin.reload(sender);
                return true;
            default:
                pm.msg(sender, "errorMessages.invalidCommand", true);
                return true;
        }

    }



    private void removeWarpSubcommand(CommandSender sender, String[] args) {

        if (args.length < 2) {
            pm.msg(sender, "errorMessages.invalidCommandLength", true);
            return;
        }

        boolean warpRemoved = plugin.getWarpRegister().removeWarp(args[1]);
        if (warpRemoved) {
            pm.msg(sender, "warpMessages.warpRemoved", true, "%warp%", args[1]);
        } else {
            pm.msg(sender, "warpMessages.warpNotFound", true, "%warp%", args[1]);
        }
    }

    private void requestSubcommand(CommandSender sender, String[] args) {

        if (args.length < 3) {
            pm.msg(sender, "errorMessages.invalidCommandLength", true);
            return;
        }

        String warpName = args[1];

        WarpRequest request = plugin.getWarpRegister().getWarpRequest(warpName);

        if (request == null) {
            pm.msg(sender, "warpMessages.warpRequestNotFound", true, "%warp%", args[1]);
            return;
        }

        switch (args[2].toLowerCase(Locale.ENGLISH)) {
            case "reject":
                // TODO - possibly refund materials to user that made the warp?
                boolean requestRemoved = plugin.getWarpRegister().removeWarpRequest(request.getName());
                if (requestRemoved) {
                    pm.msg(sender, "warpMessages.warpRequestRejected", true, "%warp%", args[1]);
                } else {
                    pm.msg(sender, "warpMessages.warpRequestNotFound", true, "%warp%", args[1]);
                }
                return;
            case "approve":
                Warp warp = new Warp(request);
                boolean warpAdded = plugin.getWarpRegister().addWarp(warp.getName(), warp);
                if (warpAdded) {
                    pm.msg(sender, "warpMessages.warpRequestApproved", true, "%warp%", warp.getName());
                    plugin.getWarpRegister().removeWarpRequest(request.getName());
                } else {
                    pm.msg(sender, "warpMessages.warpAlreadyExists", true, "%warp%", warp.getName());

                }
                return;
            default:
                pm.msg(sender, "errorMessages.invalidCommand", true);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        List<String> list = new ArrayList<>();

        switch (args.length) {

            case 1:
                if (sender.hasPermission("pwarp.admin.remove")) list.add("remove");
                if (sender.hasPermission("pwarp.admin.request")) list.add("request");
                if (sender.hasPermission("pwarp.admin.reload")) list.add("reload");
                return list;
            case 2:
                if (args[0].equalsIgnoreCase("remove") && sender.hasPermission("pwarp.admin.remove")) {
                    for (Warp warp : plugin.getWarpRegister().getWarps().values()) {
                        list.add(warp.getName());
                    }
                } else if (args[0].equalsIgnoreCase("request") && sender.hasPermission("pwarp.admin.request")) {
                    for (WarpRequest warpRequest : plugin.getWarpRegister().getWarpRequests().values()) {
                        list.add(warpRequest.getName());
                    }
                }
                return list;
            case 3:
                if (args[0].equalsIgnoreCase("request") && sender.hasPermission("pwarp.admin.request")) {
                    list.add("approve");
                    list.add("reject");
                }
                return list;
            default:
                return list;
        }
    }
}
