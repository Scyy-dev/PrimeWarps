package me.Scyy.PrimeWarps.Commands;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Warps.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
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

            case "changeowner":
                if (!sender.hasPermission("pwarp.admin.changeowner")) {
                    pm.msg(sender, "errorMessages.noPermission");
                    return true;
                }
                changeOwnerSubcommand(sender, args);
                return true;
            case "remove":
                if (!sender.hasPermission("pwarp.admin.remove")) {
                    pm.msg(sender, "errorMessages.noPermission");
                    return true;
                }
                removeWarpSubcommand(sender, args);
                return true;
            case "setwarpshard":
                if (!sender.hasPermission("pwarp.admin.setwarpshard")) {
                    pm.msg(sender, "errorMessages.noPermission");
                    return true;
                }
                if (!(sender instanceof Player)) {
                    pm.msg(sender, "errorMessages.mustBePlayer");
                    return true;
                }
                setWarpShardSubcommand(sender, args);
                return true;
            case "forceinactive":
                if (!sender.hasPermission("pwarp.admin.forceinactive")) {
                    pm.msg(sender, "errorMessages.noPermission");
                    return true;
                }
                forceInactiveSubcommand(sender, args);
                return true;
            case "setsigngui":
                if (!sender.hasPermission("pwarp.admin.forceinactive")) {
                    pm.msg(sender, "errorMessages.noPermission");
                    return true;
                }
                if (!(sender instanceof Player)) {
                    pm.msg(sender, "errorMessages.mustBePlayer");
                    return true;
                }
                setSignGUISubcommand(sender, args);
                return true;
            case "reload":
                if (!sender.hasPermission("pwarp.admin.reload")) {
                    pm.msg(sender, "errorMessages.noPermission");
                    return true;
                }
                plugin.reload(sender);
                return true;
            default:
                pm.msg(sender, "errorMessages.invalidCommand");
                return true;
        }

    }

    private void changeOwnerSubcommand(CommandSender sender, String[] args) {

        if (args.length < 3) {
            pm.msg(sender, "errorMessages.invalidCommandLength");
            return;
        }

        // Get the warp
        Warp warp = plugin.getWarpRegister().getWarp(args[1]);
        if (warp == null) {
            pm.msg(sender, "warpMessages.warpNotFound", "%warp%", args[1]);
            return;
        }

        // Get the player
        Player player = Bukkit.getPlayer(args[2]);
        if (player == null) {
            pm.msg(sender, "errorMessages.playerNotFound");
            return;
        }

        // No need to check if the player already owns the warp
        // Set the new owner, and update the last seen time for the warp
        warp.setOwner(player.getUniqueId());
        warp.setOwnerLastSeen(Instant.now());
    }

    private void removeWarpSubcommand(CommandSender sender, String[] args) {

        if (args.length < 2) {
            pm.msg(sender, "errorMessages.invalidCommandLength");
            return;
        }

        boolean warpRemoved = plugin.getWarpRegister().removeWarp(args[1]);
        if (warpRemoved) {
            pm.msg(sender, "warpMessages.warpRemoved", "%warp%", args[1]);
        } else {
            pm.msg(sender, "warpMessages.warpNotFound", "%warp%", args[1]);
        }
    }

    private void setWarpShardSubcommand(CommandSender sender, String[] args) {

        ItemStack mainHand = ((Player) sender).getInventory().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) {
            pm.msg(sender, "errorMessages.cannotUseAir");
            return;
        }

        // Clone the item
        ItemStack warpShard = mainHand.clone();
        warpShard.setAmount(1);

        plugin.getCFH().getMiscDataStorage().saveWarpToken(warpShard);
        pm.msg(sender, "warpMessages.addedWarpShard");

    }

    private void forceInactiveSubcommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            pm.msg(sender, "errorMessages.invalidCommandLength");
            return;
        }
        // Get the warp
        Warp warp = plugin.getWarpRegister().getWarp(args[1]);
        if (warp == null) {
            pm.msg(sender, "warpMessages.warpNotFound", "%warp%", args[1]);
            return;
        }
        warp.setInactive(true);
        pm.msg(sender, "warpMessages.warpInactive", "%warp%", warp.getName());
    }

    private void setSignGUISubcommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Block targetBlock = player.getTargetBlock(null,7);

        if (!(targetBlock.getState() instanceof Sign)) {
            pm.msg(sender, "errorMessages.mustBeSign");
            return;
        }

        Sign sign = (Sign) targetBlock.getState();
        plugin.getSignManager().markAsGUI(sign);
        plugin.getCFH().getMiscDataStorage().setSignLocation(player.getWorld().getName(), sign.getLocation());

        String signLoc = "[" + sign.getLocation().getWorld().getName() + ": " + sign.getLocation().getBlockX() + ", "
                + sign.getLocation().getBlockY() + ", " + sign.getLocation().getBlockZ() + "]";

        pm.msg(sender, "otherMessages.setSignGUI", "%loc%", signLoc);

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        List<String> list = new ArrayList<>();

        switch (args.length) {

            case 1:
                if (sender.hasPermission("pwarp.admin.changeowner")) list.add("changeowner");
                if (sender.hasPermission("pwarp.admin.remove")) list.add("remove");
                if (sender.hasPermission("pwarp.admin.forceinactive")) list.add("forceinactive");
                if (sender.hasPermission("pwarp.admin.reload")) list.add("reload");
                if (sender.hasPermission("pwarp.admin.setwarpshard")) list.add("setwarpshard");
                if (sender.hasPermission("pwarp.admin.setsigngui")) list.add("setsigngui");
                return list;
            case 2:
                if (args[0].equalsIgnoreCase("remove") && sender.hasPermission("pwarp.admin.remove")) {
                    for (Warp warp : plugin.getWarpRegister().getWarps().values()) {
                        list.add(warp.getName());
                    }
                } else if (args[0].equalsIgnoreCase("changeowner") && sender.hasPermission("pwarp.admin.changeowner")) {
                    for (Warp warp : plugin.getWarpRegister().getWarps().values()) {
                        list.add(warp.getName());
                    }
                } else if (args[0].equalsIgnoreCase("forceinactive") && sender.hasPermission("pwarp.admin.forceinactive"))  {
                    for (Warp warp : plugin.getWarpRegister().getWarps().values()) {
                        list.add(warp.getName());
                    }
                }
                return list;
            case 3:
                if (args[0].equalsIgnoreCase("changeowner") && sender.hasPermission("pwarp.admin.request")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        list.add(player.getName());
                    }
                }
                return list;
            default:
                return list;
        }
    }
}
