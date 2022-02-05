package me.scyphers.minecraft.primewarps.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemStackUtils {

    public static void removeItem(Player player, ItemStack item, int amount) {

        int removedCounter = amount;

        for (ItemStack itemStack : player.getInventory().getStorageContents()) {

            if (itemStack.isSimilar(item)) {

                if (itemStack.getAmount() >= removedCounter) {
                    itemStack.setAmount(itemStack.getAmount() - removedCounter);
                    return;
                } else {
                    removedCounter -= itemStack.getAmount();
                    itemStack.setAmount(0);
                }

            }

        }

    }

}
