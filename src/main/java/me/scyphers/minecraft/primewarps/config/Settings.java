package me.scyphers.minecraft.primewarps.config;

import me.scyphers.scycore.api.PluginSettings;
import me.scyphers.scycore.config.ConfigFile;
import me.scyphers.scycore.config.FileManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class Settings extends ConfigFile implements PluginSettings {

    private int saveTicks;

    // Warp 'popularity' algorithm settings
    private int uniqueHitsWeighting, warpUptimeWeighting, ownerDowntimeWeighting, weeklyVisitorAverageWeighting;

    // Warp cost settings
    private int createWarpCost, moveWarpCost, renameWarpCost, reactivateWarpCost;

    // Island caps for warps
    private int prestigePerWarp, maximumWarps;

    // Warp categories
    private Map<String, Material> warpMaterials;
    private String defaultCategory;

    // Valid warp worlds
    private List<String> validWorlds;

    // Misc Warp settings
    private int inactiveDays;


    public Settings(FileManager manager) throws Exception {
        super(manager, "config.yml");
    }

    @Override
    public void load(YamlConfiguration file, YamlConfiguration fileDefaults) throws Exception {
        this.saveTicks = file.getInt("fileSaveTicks", 72000);

        this.uniqueHitsWeighting = file.getInt("uniqueHitsWeighting", 1);
        this.warpUptimeWeighting = file.getInt("warpUptimeWeighting", 1);
        this.ownerDowntimeWeighting = file.getInt("ownerDowntimeWeighting", 1);
        this.weeklyVisitorAverageWeighting = file.getInt("weeklyVisitorAverageWeighting", 1);

        this.createWarpCost = file.getInt("createWarpCost", 10);
        this.moveWarpCost = file.getInt("moveWarpCost", 5);
        this.renameWarpCost = file.getInt("renameWarpCost", 5);
        this.reactivateWarpCost = file.getInt("reactivateWarpCost", 10);

        this.prestigePerWarp = file.getInt("prestigePerWarp", 10);
        this.maximumWarps = file.getInt("maximumWarps", 5);

        ConfigurationSection categories = file.getConfigurationSection("categories");
        if (categories == null) this.warpMaterials = Collections.emptyMap();
        else {
            this.warpMaterials = new HashMap<>();
            for (String categoryName : categories.getKeys(false)) {
                String rawMaterial = categories.getString(categoryName);
                Material material = Material.valueOf(rawMaterial);
                warpMaterials.put(categoryName, material);
            }
        }
        this.defaultCategory = file.getString("defaultCategory", "default");

        this.validWorlds = file.getStringList("worlds");

        this.inactiveDays = file.getInt("inactiveDays", 14);


    }

    @Override
    public int getSaveTicks() {
        return saveTicks;
    }

    public int getUniqueHitsWeighting() {
        return uniqueHitsWeighting;
    }

    public int getWarpUptimeWeighting() {
        return warpUptimeWeighting;
    }

    public int getOwnerDowntimeWeighting() {
        return ownerDowntimeWeighting;
    }

    public int getWeeklyVisitorAverageWeighting() {
        return weeklyVisitorAverageWeighting;
    }

    public int getCreateWarpCost() {
        return createWarpCost;
    }

    public int getMoveWarpCost() {
        return moveWarpCost;
    }

    public int getRenameWarpCost() {
        return renameWarpCost;
    }

    public int getReactivateWarpCost() {
        return reactivateWarpCost;
    }

    public int getPrestigePerWarp() {
        return prestigePerWarp;
    }

    public int getMaximumWarps() {
        return maximumWarps;
    }

    public Set<String> getCategories() {
        return warpMaterials.keySet();
    }

    public Collection<Material> getCategoryMaterials() {
        return warpMaterials.values();
    }

    public Material getCategoryMaterial(String category) {
        return warpMaterials.getOrDefault(category, Material.STONE);
    }

    public String getDefaultCategory() {
        return defaultCategory;
    }

    public int getInactiveDays() {
        return inactiveDays;
    }

}
