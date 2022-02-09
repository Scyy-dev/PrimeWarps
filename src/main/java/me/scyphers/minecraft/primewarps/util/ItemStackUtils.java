package me.scyphers.minecraft.primewarps.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemStackUtils {

    public static void removeItem(@NotNull Player player, @NotNull ItemStack item, int amount) {

        int removedCounter = amount;

        for (ItemStack itemStack : player.getInventory().getStorageContents()) {

            if (item.isSimilar(itemStack)) {

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
