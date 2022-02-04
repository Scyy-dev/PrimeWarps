package me.scyphers.minecraft.primewarps.config;

import me.scyphers.scycore.config.ConfigStorageFile;
import me.scyphers.scycore.config.FileManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;

public class MiscDataFile extends ConfigStorageFile {

    private ItemStack warpToken;

    private Instant lastResetTime;

    public MiscDataFile(FileManager manager) throws Exception {
        super(manager, "data.yml");
    }

    @Override
    public void load(YamlConfiguration file) throws Exception {
        this.warpToken = file.getItemStack("warpToken", new ItemStack(Material.PRISMARINE_SHARD));
        this.lastResetTime = Instant.ofEpochMilli(file.getLong("weeklyTimer", 0));
    }

    @Override
    public void saveData(YamlConfiguration file) throws Exception {
        file.set("warpToken", this.warpToken);
        file.set("weeklyTimer", this.lastResetTime.toEpochMilli());
    }

    public ItemStack getWarpToken() {
        return warpToken;
    }

    public Instant getLastResetTime() {
        return lastResetTime;
    }

}
