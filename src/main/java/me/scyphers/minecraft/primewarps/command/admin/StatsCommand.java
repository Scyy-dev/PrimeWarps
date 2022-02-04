package me.scyphers.minecraft.primewarps.command.admin;

import me.Scyy.PrimeWarps.Warps.Warp;
import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.scycore.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class StatsCommand extends BaseCommand {
    
    private final PrimeWarps plugin;
    
    public StatsCommand(PrimeWarps plugin, String permission) {
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

        String warpOwner;
        OfflinePlayer owner = plugin.getServer().getOfflinePlayer(warp.getOwner());
        if (!owner.hasPlayedBefore() && !owner.isOnline()) warpOwner = warp.getOwnerName();
        else warpOwner = owner.getName();

        StringBuilder weeklyVisits = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            weeklyVisits.append("w").append(i).append(": ").append(warp.getWeeklyVisitors(i).size()).append(", ");
        }
        weeklyVisits.delete(weeklyVisits.length() - 1, weeklyVisits.length());
        m.chatList(sender, "stats",
                "%warp%", warp.getName(),
                "%category%", warp.getCategory(),
                "%owner%", warpOwner,
                "%visits%", Integer.toString(warp.getUniqueVisitorCount()),
                "%weekly%", weeklyVisits.toString(),
                "%weeklyAverage%", Integer.toString(warp.getWeeklyAverage()),
                "%ownerUUID%", warp.getOwner().toString(),
                "%warpUptime%", Integer.toString(warp.getDaysSinceCreation()));

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
