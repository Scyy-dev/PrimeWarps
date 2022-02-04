package me.scyphers.minecraft.primewarps.command.admin;

import me.Scyy.PrimeWarps.Warps.Warp;
import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.scycore.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class SearchCommand extends BaseCommand {
    
    private final PrimeWarps plugin;
    
    public SearchCommand(PrimeWarps plugin, String permission) {
        super(plugin, permission, 2);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        if (!player.hasPlayedBefore() && !player.isOnline()) {
            m.chat(sender, "errorMessages.playerNotFound");
            return true;
        }

        List<Warp> warps = plugin.getWarpRegister().getWarps().values().stream()
                .filter((warp -> warp.getOwner().equals(player.getUniqueId()))).collect(Collectors.toList());

        m.chat(sender, "warpMessages.warplist", "%player%", player.getName());

        StringBuilder builder = new StringBuilder();

        for (Warp warp : warps) {
            if (warp.isInactive()) builder.append(warp.getName()).append(" (inactive), ");
            else builder.append(warp.getName()).append(", ");
        }

        if (builder.length() > 0) {
            builder.delete(builder.length() - 2, builder.length());
        }

        sender.sendMessage(builder.toString());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] strings) {
        // TODO - return a list
        for (Player player : Bukkit.getOnlinePlayers()) {
            list.add(player.getName());
        }
    }
}
