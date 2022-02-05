package me.scyphers.minecraft.primewarps.warps;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public record WarpRequestResponse(String warpName, UUID islandUUID, UUID warpRequester, boolean success, boolean refundShards) {

    public boolean refundShardsFor(Player player, ItemStack warpShard, int amount) {
        if (!refundShards) return false;

        ItemStack shard = warpShard.clone();
        shard.setAmount(amount);

        player.getInventory().addItem(shard);
        return true;
    }

}
