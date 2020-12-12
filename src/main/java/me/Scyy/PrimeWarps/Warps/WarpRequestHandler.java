package me.Scyy.PrimeWarps.Warps;

import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class WarpRequestHandler {

    private final UUID owner;

    private final String warpName;

    private boolean refundWarpShards;

    private final String requestMessage;

    public WarpRequestHandler(UUID owner, String warpName, boolean refundWarpShards, String requestMessage) {
        this.owner = owner;
        this.warpName = warpName;
        this.refundWarpShards = refundWarpShards;
        this.requestMessage = requestMessage;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getWarpName() {
        return warpName;
    }

    public boolean isRefundWarpShards() {
        return refundWarpShards;
    }

    public void setRefundWarpShards(boolean refundWarpShards) {
        this.refundWarpShards = refundWarpShards;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void refundWarpShards(Player player, Plugin plugin) {
        if (!this.isRefundWarpShards()) return;
        ItemStack warpToken = plugin.getCFH().getMiscDataStorage().getWarpToken();
        warpToken.setAmount(plugin.getCFH().getSettings().getWarpTokenCount());
        if (!(player.getInventory().firstEmpty() == -1)) {
            player.getInventory().addItem(warpToken);
            this.setRefundWarpShards(false);
        } else {
            plugin.getCFH().getPlayerMessenger().msg(player, "warpMessages.invFullCannotRefundWarpShards");
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarpRequestHandler handler = (WarpRequestHandler) o;
        return owner.equals(handler.owner) && warpName.equals(handler.warpName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, warpName);
    }
}
