package me.scyphers.minecraft.primewarps.config;

import me.scyphers.minecraft.primewarps.warps.WarpRequest;
import me.scyphers.minecraft.primewarps.warps.WarpRequestRegister;
import me.scyphers.scycore.config.ConfigStorageFile;
import me.scyphers.scycore.config.FileManager;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

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
            UUID uuid = UUID.fromString(section.getString(warpName + ".islandUUID", "INVALID_UUID"));
            Location location = section.getLocation(warpName + ".location");
            String category = section.getString(warpName + ".category", "INVALID_CATEGORY");
            Instant dateCreated = Instant.ofEpochMilli(file.getLong(warpName + ".dateCreated", 0));
            warpRequests.put(warpName, new WarpRequest(warpName, uuid, location, category, dateCreated));
        }


    }

    @Override
    public void saveData(YamlConfiguration file) throws Exception {

        file.set("warpRequests", null);

        for (String warpRequestName : warpRequests.keySet()) {

            WarpRequest request = warpRequests.get(warpRequestName);

            file.set("warpRequests." + warpRequestName + ".islandUUID", request.islandUUID());
            file.set("warpRequests." + warpRequestName + ".location", request.location());
            file.set("warpRequests." + warpRequestName + ".category", request.category());
            file.set("warpRequests." + warpRequestName + ".dateCreated", request.dateCreated());
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

}
