package me.scyphers.minecraft.primewarps.command.admin;

import me.Scyy.PrimeWarps.Warps.Warp;
import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.scycore.BasePlugin;
import me.scyphers.scycore.command.BaseCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RemoveWarpCommand extends BaseCommand {

    private final PrimeWarps plugin;

    public RemoveWarpCommand(PrimeWarps plugin, String permission) {
        super(plugin, permission, 2);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {

        // Remove the warp
        boolean warpRemoved = plugin.getWarpRegister().removeWarp(args[1]);

        // Send a message based on removal success
        if (warpRemoved) m.chat(sender, "warpMessages.warpRemoved", "%warp%", args[1]);
        else m.chat(sender, "warpMessages.warpNotFound", "%warp%", args[1]);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] strings) {
        // TODO - return a list
        for (Warp warp : plugin.getWarpRegister().getWarps().values()) {
            list.add(warp.getName());
        }
    }
}
