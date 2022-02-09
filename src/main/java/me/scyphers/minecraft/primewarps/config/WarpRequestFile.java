package me.scyphers.minecraft.primewarps.config;

import me.scyphers.minecraft.primewarps.warps.WarpRequest;
import me.scyphers.minecraft.primewarps.warps.WarpRequestRegister;
import me.scyphers.scycore.config.ConfigStorageFile;
import me.scyphers.scycore.config.FileManager;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class WarpRequestFile extends ConfigStorageFile implements WarpRequestRegister {

    private Map<String, WarpRequest> warpRequests;

    public WarpRequestFile(FileManager manager) throws Exception {
        super(manager, "requests.yml");
    }

    @Override
    public void load(YamlConfiguration file) throws Exception {

        this.warpRequests = new HashMap<>();

        ConfigurationSection section = file.getConfigurationSection("warpRequests");
        if (section == null || section.getKeys(false).size() == 0) return;

        for (String warpName : section.getKeys(false)) {
            UUID islandUUID = UUID.fromString(section.getString(warpName + ".islandUUID", "INVALID_UUID"));
            UUID requesterUUID = UUID.fromString(section.getString(warpName + ".requesterUUID", "INVALID_UUID"));
            Location location = section.getLocation(warpName + ".location");
            String category = section.getString(warpName + ".category", "INVALID_CATEGORY");
            Instant dateCreated = Instant.ofEpochMilli(file.getLong(warpName + ".dateCreated", 0));
            warpRequests.put(warpName, new WarpRequest(warpName, islandUUID, requesterUUID, location, category, dateCreated));
        }

    }

    @Override
    public void saveData(YamlConfiguration file) throws Exception {

        file.set("warpRequests", null);

        for (String warpRequestName : warpRequests.keySet()) {

            WarpRequest request = warpRequests.get(warpRequestName);

            file.set("warpRequests." + warpRequestName + ".islandUUID", request.islandUUID().toString());
            file.set("warpRequests." + warpRequestName + ".requesterUUID", request.requester().toString());
            file.set("warpRequests." + warpRequestName + ".location", request.location());
            file.set("warpRequests." + warpRequestName + ".category", request.category());
            file.set("warpRequests." + warpRequestName + ".dateCreated", request.dateCreated().toEpochMilli());
        }

    }

    @Override
    public boolean warpRequestExists(String warpName) {
        return warpRequests.containsKey(warpName.toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean addRequest(String warpName, WarpRequest request) {
        if (warpRequests.containsKey(warpName)) return false;
        warpRequests.put(warpName, request);
        return true;
    }

    @Override
    public Collection<WarpRequest> getRequests() {
        return warpRequests.values();
    }

    @Override
    public void removeWarpRequest(String warpName) {
        this.warpRequests.remove(warpName);
    }

    @Override
    public List<WarpRequest> getRequests(UUID islandUUID) {
        return warpRequests.values().stream().filter(warpRequest -> warpRequest.islandUUID().equals(islandUUID)).collect(Collectors.toList());
    }
}
