package me.scyphers.minecraft.primewarps.config;

import me.scyphers.minecraft.primewarps.warps.Warp;
import me.scyphers.minecraft.primewarps.warps.WarpRegister;
import me.scyphers.scycore.config.ConfigStorageFile;
import me.scyphers.scycore.config.FileManager;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class WarpFile extends ConfigStorageFile implements WarpRegister {

    private Map<String, Warp> warps;

    // TODO - resolve issue of loading locations before world is loaded

    public WarpFile(FileManager manager) throws Exception {
        super(manager, "warps.yml");
    }

    @Override
    public void load(YamlConfiguration file) throws Exception {
        this.warps = new HashMap<>();

        ConfigurationSection warps = file.getConfigurationSection("warps");
        if (warps == null) return;

        for (String warpName : warps.getKeys(false)) {

            // Load non-collection based warp information
            UUID islandUUID = UUID.fromString(warps.getString(warpName + ".islandUUID", "INVALID_UUID"));
            Location location = warps.getLocation(warpName + ".location");
            String category = warps.getString(warpName + ".category");
            Instant dateCreated = Instant.ofEpochMilli(warps.getLong(warpName + ".dateCreated"));
            Instant lastSeen = Instant.ofEpochMilli(warps.getLong(warpName + ".lastSeen"));
            boolean inactive = warps.getBoolean(warpName + ".inactive");

            // Load unique visitors
            Set<UUID> uniqueVisitors = new HashSet<>();
            for (String rawVisitorUUID : warps.getStringList(warpName + ".uniqueVisitors")) {
                uniqueVisitors.add(UUID.fromString(rawVisitorUUID));
            }

            // Load weekly visitors - each week is stored as a string list from 0-3 inclusive
            List<Set<UUID>> weeklyVisitors = Arrays.asList(new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
            ConfigurationSection weeklyVisitorSection = warps.getConfigurationSection("weeklyVisitors");
            if (weeklyVisitorSection != null) {
                for (int i = 0; i < 4; i++) {
                    Set<UUID> weeklyVisitorSet = weeklyVisitors.get(i);
                    for (String rawVisitorUUID : weeklyVisitorSection.getStringList("" + i)) {
                        weeklyVisitorSet.add(UUID.fromString(rawVisitorUUID));
                    }
                }
            }

            Warp warp = new Warp(warpName, islandUUID, location, category, dateCreated, lastSeen, inactive, uniqueVisitors, weeklyVisitors);
            this.warps.put(warpName, warp);
        }

    }

    @Override
    public void saveData(YamlConfiguration file) throws Exception {

        // Clear all existing warps
        file.set("warps", null);

        // Iterate over each warp
        for (String warpName : warps.keySet()) {

            Warp warp = warps.get(warpName);
            file.set("warps." + warpName + ".islandUUID", warp.getIslandUUID());
            file.set("warps." + warpName + ".location", warp.getLocation());
            file.set("warps." + warpName + ".category", warp.getCategory());
            file.set("warps." + warpName + ".dateCreated", warp.getDateCreated().toEpochMilli());
            file.set("warps." + warpName + ".ownerLastSeen", warp.getLastSeen().toEpochMilli());
            file.set("warps." + warpName + ".inactive", warp.isInactive());

            // Convert unique visitors to a list of strings and save the list
            List<String> uniqueVisitors = warp.getUniqueVisitors().stream().map(UUID::toString).collect(Collectors.toList());
            file.set("warps." + warpName + ".uniqueVisitors", uniqueVisitors);

            // Convert weekly visitors to 4 lists of strings and save each list
            List<Set<UUID>> weeklyVisitors = warp.getWeeklyVisitors();
            for (int i = 0; i < 4; i++) {
                List<String> weeklyVisitorList = weeklyVisitors.get(i).stream().map(UUID::toString).collect(Collectors.toList());
                file.set("warps." + warpName + ".weeklyVisitors." + i, weeklyVisitorList);
            }

        }

    }

    @Override
    public boolean warpExists(String warpName) {
        return warps.containsKey(warpName.toLowerCase(Locale.ROOT));
    }

    @Override
    public Collection<Warp> getAllWarps() {
        return warps.values();
    }

    @Override
    public void removeWarp(String warpName) {
        warps.remove(warpName);
    }

    @Override
    public List<Warp> getIslandWarps(UUID islandUUID) {
        return warps.values().stream().filter(warp -> warp.getIslandUUID().equals(islandUUID)).collect(Collectors.toList());
    }

    @Override
    public List<Warp> getWarpsByCategory(String category) {
        if (category == null || category.equals("")) return new ArrayList<>(warps.values());
        return warps.values().stream().filter(warp -> warp.getCategory().equals(category)).collect(Collectors.toList());
    }

    @Override
    public void addWarp(String warpName, Warp warp) {
        this.warps.put(warpName, warp);
    }

    @Override
    public long getWarpCount(UUID islandUUID) {
        return warps.values().stream().filter(warp -> warp.getIslandUUID().equals(islandUUID)).count();
    }

    @Override
    public Warp getWarp(String warpName) {
        return warps.get(warpName);
    }
}
