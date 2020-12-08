package me.Scyy.PrimeWarps.Warps;

import org.bukkit.Location;

import java.util.Date;
import java.util.UUID;

public class WarpRequest {

    private final String name;

    private final UUID owner;

    private final Location location;

    private final Date dateCreated;

    public WarpRequest(UUID owner, String name, Location location) {
        this.owner = owner;
        this.name = name;
        this.location = location;
        this.dateCreated = new Date();
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public UUID getOwner() {
        return owner;
    }
}
