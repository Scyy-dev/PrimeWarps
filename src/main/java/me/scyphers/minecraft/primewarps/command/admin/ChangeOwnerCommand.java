package me.scyphers.minecraft.primewarps.command.admin;

import me.Scyy.PrimeWarps.Warps.Warp;
import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.scycore.BasePlugin;
import me.scyphers.scycore.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class ChangeOwnerCommand extends BaseCommand {

    private final PrimeWarps plugin;

    public ChangeOwnerCommand(PrimeWarps plugin, String permission) {
        super(plugin, permission, 3);
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

        // Get the player
        // TODO - change to use offline player to allow changing owner to offline players
        Player player = Bukkit.getPlayer(args[2]);
        if (player == null) {
            m.chat(sender, "errorMessages.playerNotFound", "%player%", args[2]);
            return true;
        }

        // No need to check if the player already owns the warp
        // Set the new owner, and update the last seen time for the warp
        warp.setOwner(player.getUniqueId());
        warp.setOwnerLastSeen(Instant.now());
        m.chat(sender, "warpMessages.changedOwner", "%warp%", warp.getName(), "%owner%", player.getName());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args) {
        // TODO - return a list
        switch (args.length) {
            case 2:
                for (Warp warp : plugin.getWarpRegister().getWarps().values()) {
                    list.add(warp.getName());
                }
            case 3:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    list.add(player.getName());
                }
            default:
                return Collections.emptyList();
        }
    }

}
