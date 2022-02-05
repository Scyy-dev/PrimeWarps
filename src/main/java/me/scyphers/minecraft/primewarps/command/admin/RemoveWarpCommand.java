package me.scyphers.minecraft.primewarps.command.admin;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.scycore.command.BaseCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RemoveWarpCommand extends BaseCommand {

    private final PrimeWarps plugin;

    public RemoveWarpCommand(PrimeWarps plugin, String permission) {
        super(plugin, permission, 2);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {

        String warpName = args[1].toLowerCase(Locale.ROOT);

        if (!plugin.getWarps().warpExists(warpName)) {
            m.chat(sender, "warpMessages.warpNotFound", "%warp%", args[1]);
            return true;
        }

        // Remove the warp
        plugin.getWarps().removeWarp(warpName);

        m.chat(sender, "warpMessages.warpRemoved", "%warp%", args[1]);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return plugin.getWarps().getAllWarps().stream().map(Warp::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
