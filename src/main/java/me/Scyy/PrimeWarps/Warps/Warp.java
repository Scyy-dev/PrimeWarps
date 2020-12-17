package me.Scyy.PrimeWarps.Warps;

import org.bukkit.Location;

import java.time.Instant;
import java.util.*;

public class Warp {

    protected final String name;

    protected UUID owner;

    protected final Location location;

    protected String category;

    protected final Instant dateCreated;

    protected Instant ownerLastSeen;

    protected boolean inactive;

    protected final Set<UUID> uniqueVisitors;

    /**
     * For loading a warp from config
     * @param name name of the warp
     * @param owner UUID of the player who owns this warp
     * @param location location of the warp
     * @param dateCreated when the warp was created
     * @param uniqueVisitors collection of all unique visits to the warp
     */
    public Warp(String name, UUID owner, Location location, String category, Instant dateCreated, Instant ownerLastSeen, boolean inactive, Set<UUID> uniqueVisitors) {
        this.name = name.toLowerCase(Locale.ENGLISH);
        this.owner = owner;
        this.location = location;
        this.category = category;
        this.dateCreated = dateCreated;
        this.ownerLastSeen = ownerLastSeen;
        this.inactive = inactive;
        this.uniqueVisitors = uniqueVisitors;
    }

    public Warp(WarpRequest request) {
        this.name = request.getName();
        this.owner = request.getOwner();
        this.location = request.getLocation();
        this.category = "default";
        this.dateCreated = request.getDateCreated();
        this.ownerLastSeen = Instant.now();
        this.inactive = false;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public Instant getOwnerLastSeen() {
        return ownerLastSeen;
    }

    public void setOwnerLastSeen(Instant ownerLastSeen) {
        this.ownerLastSeen = ownerLastSeen;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public void setInactive(int daysAgo) {
        this.inactive = testInactivity(daysAgo);
    }

    public boolean testInactivity(int daysAgo) {
        // Subtract daysAgo days away from this second
        Instant inactiveMarker = Instant.now().minusSeconds(daysAgo * 86400L);
        return inactiveMarker.isAfter(ownerLastSeen);
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
