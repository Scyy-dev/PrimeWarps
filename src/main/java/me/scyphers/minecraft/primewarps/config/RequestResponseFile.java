package me.scyphers.minecraft.primewarps.config;

import me.scyphers.minecraft.primewarps.warps.RequestResponseManager;
import me.scyphers.minecraft.primewarps.warps.WarpRequestResponse;
import me.scyphers.scycore.config.ConfigStorageFile;
import me.scyphers.scycore.config.FileManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class RequestResponseFile extends ConfigStorageFile implements RequestResponseManager {

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
                boolean success = responses.getBoolean(rawIslandUUID + "." + key + ".success", false);
                boolean refundShards = responses.getBoolean(rawIslandUUID + "." + key + ".refundShards", false);

                WarpRequestResponse requestResponse = new WarpRequestResponse(warpName, islandUUID, requesterUUID, success, refundShards);
                responseList.add(requestResponse);

            }

            this.requestResponses.put(islandUUID, responseList);

        }

    }

    @Override
    public void saveData(YamlConfiguration file) throws Exception {

        for (UUID playerUUID : requestResponses.keySet()) {

            List<WarpRequestResponse> responses = requestResponses.get(playerUUID);

            for (int key = 0; key < responses.size(); key++) {

                WarpRequestResponse response = responses.get(key);

                file.set(playerUUID + "." + key + ".warpName", response.warpName());
                file.set(playerUUID + "." + key + ".requesterUUID", response.warpRequester().toString());
                file.set(playerUUID + "." + key + ".success", response.success());
                file.set(playerUUID + "." + key + ".refundShards", response.refundShards());

            }

        }

    }

    @Override
    public void scheduleResponse(UUID playerUUID, WarpRequestResponse response) {
        List<WarpRequestResponse> responses = this.hasResponse(playerUUID) ? this.getResponses(playerUUID) : new ArrayList<>();
        responses.add(response);
        this.requestResponses.put(playerUUID, responses);
    }

    @Override
    public List<WarpRequestResponse> getResponses(UUID playerUUID) {
        return new ArrayList<>(requestResponses.get(playerUUID));
    }

    @Override
    public boolean hasResponse(UUID playerUUID) {
        return requestResponses.containsKey(playerUUID);
    }

    @Override
    public void clearResponses(UUID playerUUID) {
        this.requestResponses.remove(playerUUID);
    }

}
