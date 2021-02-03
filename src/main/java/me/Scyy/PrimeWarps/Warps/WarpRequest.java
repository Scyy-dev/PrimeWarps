package me.Scyy.PrimeWarps.Warps;

import org.bukkit.Location;

import java.time.Instant;
import java.util.UUID;

public class WarpRequest {

    private final String name;

    private final String ownerName;

    private final UUID owner;

    private final Location location;

    private final Instant dateCreated;

    public WarpRequest(String name, String ownerName, UUID owner, Location location) {
        this.owner = owner;
        this.ownerName = ownerName;
        this.name = name;
        this.location = location;
        this.dateCreated = Instant.now();
    }

    public String getName() {
        return name;
    }

    public String getOwnerName() {
        return ownerName;
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
        return "Warp{" +
                "name='" + name + '\'' +
                ", owner=" + owner +
                ", location=" + location +
                '}';
    }
}
