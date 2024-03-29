package me.scyphers.minecraft.primewarps.command.admin;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.scycore.command.BaseCommand;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ForceInactiveCommand extends BaseCommand {
    
    private final PrimeWarps plugin;
    
    public ForceInactiveCommand(PrimeWarps plugin, String permission) {
        super(plugin, permission, 2);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {

        // Check if the warp exists
        String warpName = args[1].toLowerCase(Locale.ROOT);
        if (!plugin.getWarps().warpExists(warpName)) {
            m.chat(sender, "warpMessages.warpNotFound", "%warp%", args[1]);
            return true;
        }

        // Check if the player is toggling inactive state
        boolean inactive = true;
        if (args.length >= 3) {
            inactive = args[2].equalsIgnoreCase("inactive");
        }
        
        // Set the warp as inactive
        Warp warp = plugin.getWarps().getWarp(args[1]);
        warp.setInactive(inactive);

        m.chat(sender, "warpMessages.warpInactive", "%warp%", warp.getName(), "%inactive%", inactive ? "INACTIVE" : "ACTIVE");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return plugin.getWarps().getAllWarps().stream().map(Warp::getName).collect(Collectors.toList());
        }
        if (args.length == 2) {
            return Arrays.asList("active", "inactive");
        }
        return Collections.emptyList();
    }
}
