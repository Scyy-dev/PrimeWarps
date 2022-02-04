package me.scyphers.minecraft.primewarps.command.admin;

import me.Scyy.PrimeWarps.Warps.Warp;
import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.scycore.BasePlugin;
import me.scyphers.scycore.command.BaseCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ForceInactiveCommand extends BaseCommand {
    
    private final PrimeWarps plugin;
    
    public ForceInactiveCommand(PrimeWarps plugin, String permission) {
        super(plugin, permission, 2);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        
        // Get the warp
        Warp warp = plugin.getWarpRegister().getWarp(args[1]);
        if (warp == null) {
            m.chat(sender, "warpMessages.warpNotFound", "%warp%", args[1]);
            return true;
        }
        warp.setInactive(true);
        m.chat(sender, "warpMessages.warpInactive", "%warp%", warp.getName());
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
