package me.scyphers.minecraft.primewarps.command;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.gui.FeaturedWarpsGUI;
import me.scyphers.minecraft.primewarps.util.WarpUtil;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.scycore.api.Messenger;
import me.scyphers.scycore.command.SimpleCommandFactory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PrimeWarpCommandFactory extends SimpleCommandFactory {

    private final PrimeWarps plugin;

    private final Messenger m;

    public PrimeWarpCommandFactory(PrimeWarps plugin) {
        super(plugin, "pwarp", sender -> {
            if (!sender.hasPermission("primewarps.warp.gui")) {
                plugin.getMessenger().chat(sender, "errorMessages.noPermission");
                return;
            }

            if (sender instanceof Player player) {
                FeaturedWarpsGUI gui = new FeaturedWarpsGUI(null, plugin, player);
                gui.open(player);
            } else {
                plugin.getMessenger().chat(sender, "errorMessages.mustBePlayer");
            }
        });
        this.plugin = plugin;
        this.m = plugin.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {

        // User is trying to command line warp
        if (!sender.hasPermission("primewarps.warp")) {
            m.chat(sender, "errorMessages.noPermission");
            return true;
        }

        if (!(sender instanceof Player player)) {
            m.chat(sender, "errorMessages.mustBePlayer");
            return true;
        }

        String warpName = args[0].toLowerCase(Locale.ROOT);
        if (!plugin.getWarps().warpExists(warpName)) {
            m.chat(sender, "warpMessages.warpNotFound", "%warp%", args[0]);
            return true;
        }

        // Get the warp from the provided name
        Warp warp = plugin.getWarps().getWarp(warpName);

        if (warp.isInactive()) {
            m.chat(sender, "warpMessages.warpInactive", "%warp%", warp.getName());
            return true;
        }

        // Warp the player
        WarpUtil.warp(player, plugin, warp);
        return true;
        
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {

        if (!sender.hasPermission("primewarps.warp") && !sender.hasPermission("primewarps.warp.gui")) return Collections.emptyList();
        if (!sender.hasPermission("primewarps.warp")) return Collections.emptyList();

        return plugin.getWarps().getAllWarps().stream().map(Warp::getName).collect(Collectors.toList());
    }
}
