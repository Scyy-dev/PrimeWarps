package me.Scyy.PrimeWarps.Warps;

import me.Scyy.PrimeWarps.Config.Settings;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;

public class Warp implements Comparable<Warp> {

    protected final String name;

    protected UUID owner;

    protected final Location location;

    protected String category;

    protected final Instant dateCreated;

    protected Instant ownerLastSeen;

    protected boolean inactive;

    protected final Set<UUID> uniqueVisitors;

    // weeklyVisitors[0] is this week, weeklyVisitors[1] is last week, weeklyVisitors[2] is 2 weeks ago etc etc
    protected WeeklyVisitors[] weeklyVisitors;

    /**
     * For loading a warp from config
     * @param name name of the warp
     * @param owner UUID of the player who owns this warp
     * @param location location of the warp
     * @param dateCreated when the warp was created
     * @param uniqueVisitors collection of all unique visits to the warp
     */
    public Warp(String name, UUID owner, Location location, String category, Instant dateCreated, Instant ownerLastSeen, boolean inactive, Set<UUID> uniqueVisitors,
                WeeklyVisitors[] weeklyVisitors) {
        this.name = name.toLowerCase(Locale.ENGLISH);
        this.owner = owner;
        this.location = location;
        this.category = category;
        this.dateCreated = dateCreated;
        this.ownerLastSeen = ownerLastSeen;
        this.inactive = inactive;
        this.uniqueVisitors = uniqueVisitors;
        this.weeklyVisitors = weeklyVisitors;
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
        this.weeklyVisitors = new WeeklyVisitors[4];
    }

    public void addVisitor(UUID visitor) {
        this.uniqueVisitors.add(visitor);
        this.weeklyVisitors[0].addVisitor(visitor);
    }

    public int getRanking() {

        long millisecondsAlive = Instant.now().toEpochMilli() - dateCreated.toEpochMilli();
        // 86400000 = 1000 milliseconds * 60 seconds * 60 minutes * 24 hours
        int daysAlive = (int) (millisecondsAlive / 86400000L);

        long millisecondsSinceOwner = Instant.now().toEpochMilli() - ownerLastSeen.toEpochMilli();
        int daysSinceOwner = (int) (millisecondsSinceOwner / 86400000L);

        int ranking = 0;

        // Calculate the ranking
        ranking += uniqueVisitors.size() * Settings.uniqueHitsWeighting;
        ranking += getWeeklyAverage() * Settings.weeklyVisitorAverageWeighting;
        ranking -= daysAlive * Settings.warpUptimeWeighting;
        ranking -= daysSinceOwner * Settings.ownerDowntimeWeighting;

        return ranking;

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

    public Set<UUID> getWeeklyVisitors(int weeksAgo) {
        if (weeksAgo > 4 || weeksAgo < 0) throw  new IllegalArgumentException("weeksAgo must be 0-4");
        return weeklyVisitors[weeksAgo].getVisitors();
    }

    public WeeklyVisitors[] getWeeklyVisitors() {
        return weeklyVisitors;
    }

    /**
     * Shuffles all the weeks in {@link Warp#weeklyVisitors} down one in the array, removing the oldest week and adding a new week
     */
    public void newWeek() {
        WeeklyVisitors[] shifted = new WeeklyVisitors[4];
        System.arraycopy(weeklyVisitors, 0, shifted, 1, 3);
        shifted[0] = new WeeklyVisitors();
        this.weeklyVisitors = shifted;
    }

    public int getWeeklyAverage() {
        int average = 0;
        for (int i = 0; i < 4; i++) {
            average += weeklyVisitors[i].visitorCount();
        }
        // Integer division - remainder automatically ignored
        return average / 4;
    }

    @Override
    public String toString() {
        return "Warp{" +
                "name='" + name + '\'' +
                ", owner=" + owner +
                ", location=" + location +
                '}';
    }

    @Override
    public int compareTo(@NotNull Warp warp) {
        return this.getRanking() - warp.getRanking();
    }

    public static WeeklyVisitors weeklyVisitors(Set<UUID> uniqueVisitors) {
        return new WeeklyVisitors(uniqueVisitors);
    }

    public static class WeeklyVisitors {

        private final Set<UUID> visitors;

        public WeeklyVisitors() {
            this(new LinkedHashSet<>());
        }

        public WeeklyVisitors(Set<UUID> visitors) {
            if (visitors == null) {
                this.visitors = new LinkedHashSet<>();
            } else {
                this.visitors = visitors;
            }
        }

        public Set<UUID> getVisitors() {
            return visitors;
        }

        public void addVisitor(UUID uuid) {
            this.visitors.add(uuid);
        }

        public int visitorCount() {
            return visitors.size();
        }

        @Override
        public String toString() {
            return "WeeklyVisitors{" +
                    "visitors=" + visitors +
                    '}';
        }
    }

}
