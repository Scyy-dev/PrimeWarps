package me.Scyy.PrimeWarps.Warps;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class Warp {

    private final String name;

    private UUID owner;

    private final Location location;

    // private Date lastUserActivity;

    private final Date dateCreated;

    public Warp(String name, UUID owner, Location location, Date dateCreated) {
        this.name = name;
        this.owner = owner;
        this.location = location;
        this.dateCreated = dateCreated;
    }

    public Warp(WarpRequest request) {
        this.name = request.getName();
        this.owner = request.getOwner();
        this.location = request.getLocation();
        this.dateCreated = request.getDateCreated();
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Location getLocation() {
        return location;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * Teleports the player to the given location. Checks if the location to teleport to is valid
     * @param player the player to teleport
     */
    public void teleport(Player player, PlayerMessenger pm) {
        World world = location.getWorld();

        // Verify the space the player takes up is safe
        assert world != null;
        if (!world.getBlockAt(location).isPassable() || !world.getBlockAt(location.getBlockX(), location.getBlockY() + 1, location.getBlockZ()).isPassable()) {
            pm.msg(player, "warpMessages.spaceBlocked", false);
        }

        if (!world.getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ()).isPassable()) {
            pm.msg(player, "warpMessages.holeInFloor", false);
        }

        player.teleport(location);

    }

    /*
    public Date getLastUserActivity() {
        return lastUserActivity;
    }

    public void setLastUserActivity(Date lastUserActivity) {
        this.lastUserActivity = lastUserActivity;
    }

    public boolean hasExpired(Date date) {
        return lastUserActivity.before(date);
    }
     */

}
