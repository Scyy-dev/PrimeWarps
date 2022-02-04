package me.scyphers.minecraft.primewarps.warps;

import me.scyphers.minecraft.primewarps.util.TimeUtil;
import org.bukkit.Location;

import java.time.Instant;
import java.util.*;

public class Warp {

    private final String name;
    private final UUID islandUUID;
    private final Location location;
    private String category;
    private final Instant dateCreated;
    private Instant lastSeen;
    private boolean inactive;

    private final Set<UUID> uniqueVisitors;
    private final List<Set<UUID>> weeklyVisitors;

    public Warp(String name, UUID islandUUID, Location location, String category, Instant dateCreated,
                Instant lastSeen, boolean inactive, Set<UUID> uniqueVisitors, List<Set<UUID>> weeklyVisitors) {
        this.name = name;
        this.islandUUID = islandUUID;
        this.location = location;
        this.category = category;
        this.dateCreated = dateCreated;
        this.lastSeen = lastSeen;
        this.inactive = inactive;
        this.uniqueVisitors = uniqueVisitors;
        this.weeklyVisitors = weeklyVisitors;
    }

    public Warp(WarpRequest warpRequest) {
        this(warpRequest.name(), warpRequest.islandUUID(), warpRequest.location(), warpRequest.category(), Instant.now(),
                Instant.now(), false, new HashSet<>(), Arrays.asList(new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>()));
    }

    public long getDaysSinceCreation() {
        return Instant.now().minusMillis(dateCreated.toEpochMilli()).toEpochMilli() / TimeUtil.DAYS_MILLI;
    }

    public long getDaysSinceLastSeen() {
        return Instant.now().minusMillis(lastSeen.toEpochMilli()).toEpochMilli() / TimeUtil.DAYS_MILLI;
    }

    public void addVisitor(UUID visitor) {
        this.uniqueVisitors.add(visitor);
        weeklyVisitors.get(0).add(visitor);
    }

    public int getRanking(int uniqueVisitorWeight, int weeklyVisitorAverageWeight, int warpUptimeWeight, int lastSeenDowntimeWeight) {

        long daysAlive = this.getDaysSinceCreation();
        long daysLastSeen = this.getDaysSinceLastSeen();

        int ranking = 0;

        ranking += uniqueVisitors.size() * uniqueVisitorWeight;
        ranking += this.getWeeklyAverage() * weeklyVisitorAverageWeight;
        ranking -= daysAlive * warpUptimeWeight;
        ranking -= daysLastSeen * lastSeenDowntimeWeight;

        return ranking;

    }

    public void startNewWeek() {
        List<Set<UUID>> adjusted = new ArrayList<>();
        adjusted.add(0, new HashSet<>());
        for (int i = 1; i < 4; i++) adjusted.add(i, weeklyVisitors.get(i - 1));
        for (int i = 0; i < 4; i++) weeklyVisitors.set(i, adjusted.get(i));
    }

    public int getWeeklyAverage() {
        int total = 0;
        for (int i = 0; i < 4; i++) {
            Set<UUID> weeklyVisitorSet = weeklyVisitors.get(i);
            total += weeklyVisitorSet.size();
        }
        return total / 4;
    }

    public String getName() {
        return name;
    }

    public UUID getIslandUUID() {
        return islandUUID;
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

    public Instant getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Instant lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public Set<UUID> getUniqueVisitors() {
        return uniqueVisitors;
    }

    public List<Set<UUID>> getWeeklyVisitors() {
        return weeklyVisitors;
    }

    @Override
    public String toString() {
        return "Warp{" +
                "name='" + name + '\'' +
                ", islandUUID=" + islandUUID +
                ", location=" + location +
                ", category='" + category + '\'' +
                ", dateCreated=" + dateCreated +
                ", lastSeen=" + lastSeen +
                ", inactive=" + inactive +
                '}';
    }

}
