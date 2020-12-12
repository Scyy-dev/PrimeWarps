package me.Scyy.PrimeWarps.Warps;

import me.Scyy.PrimeWarps.Config.PlayerMessenger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Warp {

    protected final String name;

    protected UUID owner;

    protected final Location location;

    protected final Date dateCreated;

    protected final Set<UUID> uniqueVisitors;

    /**
     * For loading a warp from config
     * @param name name of the warp
     * @param owner UUID of the player who owns this warp
     * @param location location of the warp
     * @param dateCreated when the warp was created
     * @param uniqueVisitors collection of all unique visits to the warp
     */
    public Warp(String name, UUID owner, Location location, Date dateCreated, Set<UUID> uniqueVisitors) {
        this.name = name;
        this.owner = owner;
        this.location = location;
        this.dateCreated = dateCreated;
        this.uniqueVisitors = uniqueVisitors;
    }

    public Warp(WarpRequest request) {
        this.name = request.getName();
        this.owner = request.getOwner();
        this.location = request.getLocation();
        this.dateCreated = request.getDateCreated();
        this.uniqueVisitors = new HashSet<>();
        this.uniqueVisitors.add(owner);
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

    public Set<UUID> getUniqueVisitors() {
        return uniqueVisitors;
    }

    public int getUniqueVisitorCount() {
        return uniqueVisitors.size();
    }

    @Override
    public String toString() {
        return "Warp{" +
                "name='" + name + '\'' +
                ", owner=" + owner +
                ", location=" + location +
                '}';
    }

}
