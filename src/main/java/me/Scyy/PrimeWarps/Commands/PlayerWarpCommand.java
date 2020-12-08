package me.Scyy.PrimeWarps.Commands;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Warps.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

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

            if (!sender.hasPermission("pwarp.warp")) {
                // TODO - send no permission message
                return true;
            }

            if (sender instanceof Player) {
                Player player = (Player) sender;
                // TODO - open warp GUI
            } else {
                // TODO - send must be player message
            }

            return true;

        }

        // User is trying to command line warp
        if (!sender.hasPermission("pwarp.warp")) {
            // TODO - send no permission message
            return true;
        }

        if (!(sender instanceof Player)) {
            // TODO - send must be player message
            return true;
        }

        // Get the warp from the provided name
        Warp warp = plugin.getWarpRegister().getWarp(args[0]);

        if (warp == null) {
            // TODO - send cannot find warp message
            return true;
        }

        // Teleport the player
        warp.teleport((Player) sender, pm);
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
