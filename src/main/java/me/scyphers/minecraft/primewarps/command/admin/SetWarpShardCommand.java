package me.scyphers.minecraft.primewarps.command.admin;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import me.scyphers.scycore.command.PlayerCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class SetWarpShardCommand extends PlayerCommand {
    
    private final PrimeWarps plugin;
    
    public SetWarpShardCommand(PrimeWarps plugin, String permission) {
        super(plugin, permission, 1);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(Player player, String[] strings) {
        
        // Get the item from the players hand
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) {
            m.chat(player, "errorMessages.cannotUseAir");
            return true;
        }

        // Clone the item
        ItemStack warpShard = mainHand.clone();
        warpShard.setAmount(1);

        plugin.getCFH().getMiscDataStorage().saveWarpToken(warpShard);
        m.chat(player, "warpMessages.addedWarpShard");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] strings) {
        return Collections.emptyList();
    }
}
