package me.Scyy.PrimeWarps;

import me.Scyy.PrimeWarps.GUI.Type.SignGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class SignManager {

    private final NamespacedKey signDataKey;

    private final Map<String, WorldSign> signs;

    private final Plugin plugin;

    public SignManager(Plugin plugin) {
        this.plugin = plugin;
        this.signDataKey = new NamespacedKey(plugin, "isSignGUI");
        this.signs = new HashMap<>();

        // Bukkit tasks are run as soon as the server is fully loaded, this ensures all worlds are loaded so signs can be loaded
        Bukkit.getScheduler().runTaskLater(plugin, this::loadSigns, 1);

    }

    public void loadSigns() {
        Map<String, Location> signLocations = plugin.getCFH().getMiscDataStorage().getSignLocations();
        if (signLocations == null || signLocations.size() == 0) {
            plugin.getLogger().warning("Could not load physical signs for the sign GUIs!");
            plugin.getLogger().warning("Set it with /pwa setsigngui and then reload with /pwa reload!");
            return;
        }

        // Clear the sign data
        this.signs.clear();

        // Iterate over each Location
        for (String worldName : signLocations.keySet()) {

            Location signLocation = signLocations.get(worldName);

            WorldSign sign = new WorldSign(worldName);

            // Verify the chunk is loaded
            if (!signLocation.getWorld().isChunkForceLoaded(signLocation.getBlockX(), signLocation.getBlockZ())) {
                signLocation.getChunk().setForceLoaded(true);
            }

            // Get the block at the location
            Block block = signLocation.getBlock();
            if (!(block.getState() instanceof Sign)) {
                plugin.getLogger().warning("assigned block for Sign GUI in world '" + worldName + "' isn't a sign!");
                signs.put(worldName, sign);
                continue;
            }

            sign.setSign((Sign) block.getState());
            sign.setLoaded(true);

            signs.put(worldName, sign);

        }

    }

    public boolean isValidWorld(String worldName) {
        return signs.containsKey(worldName);
    }

    public void markAsGUI(Sign block) {
        block.getPersistentDataContainer().set(signDataKey, PersistentDataType.BYTE, (byte)1);
        block.update();
    }

    public WorldSign getSign(String worldName) {
        return signs.get(worldName);
    }

    public SignGUI getGUI(String worldName) {
        WorldSign sign = signs.get(worldName);
        return sign != null ? sign.getGUI() : null;
    }

    public void setGUI(String worldName, SignGUI GUI) {
        WorldSign sign = signs.get(worldName);
        if (sign == null) {
            plugin.getLogger().warning("Sign GUI opened in world without sign");
            return;
        }
        sign.setGUI(GUI);
    }

    public void setUsage(String worldName, boolean usage) {
        WorldSign sign = signs.get(worldName);
        if (sign != null) sign.setInUse(usage);
    }

    public boolean isSignGUI(Sign sign) {
        return sign.getPersistentDataContainer().has(signDataKey, PersistentDataType.BYTE);
    }

    @Override
    public String toString() {
        return "SignManager{" +
                "signs=" + signs +
                '}';
    }
}
