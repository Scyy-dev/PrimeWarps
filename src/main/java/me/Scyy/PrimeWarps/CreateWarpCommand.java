package me.Scyy.PrimeWarps;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CreateWarpCommand implements CommandExecutor {

    private final Plugin plugin;

    public CreateWarpCommand(Plugin plugin) {this.plugin = plugin;}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {

        if (sender.hasPermission(""));
        return true;

    }
}
