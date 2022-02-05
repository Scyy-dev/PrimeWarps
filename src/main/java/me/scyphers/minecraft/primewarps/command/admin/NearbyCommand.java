package me.scyphers.minecraft.primewarps.command.admin;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.scycore.command.PlayerCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NearbyCommand extends PlayerCommand {
    
    private final PrimeWarps plugin;
    
    public NearbyCommand(PrimeWarps plugin, String permission) {
        super(plugin, permission, 2);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(Player player, String[] args) {
        int radius;
        try {
            radius = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            m.chat(player, "errorMessages.notANumber");
            return true;
        }

        Location playerLoc = player.getLocation();

        m.chat(player, "otherMessages.initialNearby");
        StringBuilder builder = new StringBuilder();

        for (Warp warp : plugin.getWarps().getAllWarps()) {
            Location location = warp.getLocation();
            if (!playerLoc.getWorld().getName().equals(location.getWorld().getName())) continue;
            double distance = playerLoc.distance(location);
            if (distance < radius) {
                builder.append(m.get("warpMessages.warpNearby", "%warp%", warp.getName(), "%dist%", Double.toString(Math.floor(distance))));
                builder.append(", ");
            }
        }

        if (builder.length() > 0) {
            builder.delete(builder.length() - 1, builder.length());
        }

        m.sendChat(player, builder.toString());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return Arrays.asList("10", "50", "100");
        return Collections.emptyList();
    }
}
