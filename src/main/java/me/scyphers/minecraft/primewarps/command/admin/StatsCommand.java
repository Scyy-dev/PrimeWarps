package me.scyphers.minecraft.primewarps.command.admin;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.scycore.command.BaseCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public class StatsCommand extends BaseCommand {
    
    private final PrimeWarps plugin;
    
    public StatsCommand(PrimeWarps plugin, String permission) {
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

        Warp warp = plugin.getWarps().getWarp(warpName);

        UUID warpOwnerUUID = plugin.getSkyblockManager().getIslandOwner(warp.getIslandUUID());
        String warpOwner = plugin.getServer().getOfflinePlayer(warpOwnerUUID).getName();

        StringBuilder weeklyVisits = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            weeklyVisits.append("w").append(i).append(": ").append(warp.getWeeklyVisitors(i).size()).append(", ");
        }
        weeklyVisits.delete(weeklyVisits.length() - 1, weeklyVisits.length());
        m.chatList(sender, "stats",
                "%warp%", warp.getName(),
                "%category%", warp.getCategory(),
                "%owner%", "" + warpOwner,
                "%visits%", "" + warp.getUniqueVisitors().size(),
                "%weekly%", "" + weeklyVisits,
                "%weeklyAverage%", "" + warp.getWeeklyAverage(),
                "%ownerUUID%", "" + warpOwnerUUID,
                "%warpUptime%", "" + warp.getDaysSinceCreation());

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
