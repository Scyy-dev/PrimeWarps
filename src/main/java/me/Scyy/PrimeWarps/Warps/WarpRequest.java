package me.Scyy.PrimeWarps.Warps;

import org.bukkit.Location;

import java.time.Instant;
import java.util.UUID;

public class WarpRequest {

    private final String name;

    private final String warpOwner;

    private final UUID owner;

    private final Location location;

    private final Instant dateCreated;

    public WarpRequest(String name, String warpOwner, UUID owner, Location location) {
        this.owner = owner;
        this.warpOwner = warpOwner;
        this.name = name;
        this.location = location;
        this.dateCreated = Instant.now();
    }

    public String getName() {
        return name;
    }

    public String getWarpOwner() {
        return warpOwner;
    }

    public Location getLocation() {
        return location;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public UUID getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "WarpRequest{" +
                "name='" + name + '\'' +
                ", warpOwner='" + warpOwner + '\'' +
                ", owner=" + owner +
                ", location=" + location +
                ", dateCreated=" + dateCreated +
                '}';
    }
}
