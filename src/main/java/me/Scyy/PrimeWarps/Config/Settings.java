package me.Scyy.PrimeWarps.Config;

import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedList;
import java.util.List;

public class Settings extends ConfigFile {

    public Settings(Plugin plugin) {
        super(plugin, "config.yml");
    }

    public int getCreateWarpCost() {
        return config.getInt("createWarpCost", 10);
    }

    public int getMoveWarpCost() {
        return config.getInt("moveWarpCost", 5);
    }

    public int getRenameWarpCost() {
        return config.getInt("renameWarpCost", 5);
    }

    public int getReactivateWarpCost() {
        return config.getInt("reactivateWarpCost", 10);
    }

    public int getInactiveDayMeasure() {
        return config.getInt("inactiveDays", 30);
    }

    public List<String> getCategories() {
        ConfigurationSection categorySection = config.getConfigurationSection("categories");
        if (categorySection != null) return new LinkedList<>(categorySection.getKeys(false));
        else {
            plugin.getLogger().warning("Could not load categories!");
            return new LinkedList<>();
        }
    }

    public Material getCategoryMaterial(String category) {
        return Material.valueOf(config.getString("categories." + category, "ACACIA_BOAT"));
    }

    public List<Material> getCategoryMaterials() {

        List<Material> categories = new LinkedList<>();

        for (String category : this.getCategories()) {
            categories.add(this.getCategoryMaterial(category));
        }

        return categories;

    }

    public String getDefaultCategory() {
        return config.getString("defaultCategory", "default");
    }

    public List<String> getWorlds() {
        return config.getStringList("worlds");
    }
}
