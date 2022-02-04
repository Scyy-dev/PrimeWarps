package me.scyphers.minecraft.primewarps.config;

import me.scyphers.minecraft.primewarps.warps.WarpRequestResponse;
import me.scyphers.scycore.config.ConfigStorageFile;
import me.scyphers.scycore.config.FileManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class RequestResponseFile extends ConfigStorageFile {

    private Map<UUID, List<WarpRequestResponse>> requestResponses;

    public RequestResponseFile(FileManager manager) throws Exception {
        super(manager, "responses.yml");
    }

    @Override
    public void load(YamlConfiguration file) throws Exception {

        this.requestResponses = new HashMap<>();

        ConfigurationSection responses = file.getConfigurationSection("responses");
        if (responses == null || responses.getKeys(false).size() == 0) return;

        for (String rawIslandUUID : responses.getKeys(false)) {

            ConfigurationSection islandResponses = responses.getConfigurationSection(rawIslandUUID);
            if (islandResponses == null || islandResponses.getKeys(false).size() == 0) continue;

            List<WarpRequestResponse> responseList = new ArrayList<>();

            UUID islandUUID = UUID.fromString(rawIslandUUID);

            for (String key : islandResponses.getKeys(false)) {

                String warpName = islandResponses.getString(rawIslandUUID + "." + key + ".warpName", "INVALID_WARP_NAME");
                UUID requesterUUID = UUID.fromString(responses.getString(rawIslandUUID + "." + key + ".requesterUUID", "INVALID_UUID"));
                boolean refundShards = responses.getBoolean(rawIslandUUID + "." + key + ".refundShards", false);

                WarpRequestResponse requestResponse = new WarpRequestResponse(warpName, islandUUID, requesterUUID, refundShards);
                responseList.add(requestResponse);

            }

            this.requestResponses.put(islandUUID, responseList);

        }

    }

    @Override
    public void saveData(YamlConfiguration yamlConfiguration) throws Exception {

    }
}
