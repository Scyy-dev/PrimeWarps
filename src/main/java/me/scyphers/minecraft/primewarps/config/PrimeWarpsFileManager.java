package me.scyphers.minecraft.primewarps.config;

import me.scyphers.scycore.BasePlugin;
import me.scyphers.scycore.config.SimpleFileManager;

public class PrimeWarpsFileManager extends SimpleFileManager {

    private final WarpFile warpFile;
    private final WarpRequestFile warpRequestFile;
    private final RequestResponseFile requestResponseFile;
    private final MiscDataFile miscDataFile;
    private final Settings settings;

    public PrimeWarpsFileManager(BasePlugin plugin) throws Exception {
        super(plugin);
        this.warpFile = new WarpFile(this);
        this.warpRequestFile = new WarpRequestFile(this);
        this.requestResponseFile = new RequestResponseFile(this);
        this.miscDataFile = new MiscDataFile(this);
        this.settings = new Settings(this);
    }

    @Override
    public void saveAll() throws Exception {
        super.saveAll();
        warpFile.save();
        warpRequestFile.save();
        miscDataFile.save();
    }

    public WarpFile getWarpsFile() {
        return warpFile;
    }

    public WarpRequestFile getWarpRequestFile() {
        return warpRequestFile;
    }

    public RequestResponseFile getRequestResponseFile() {
        return requestResponseFile;
    }

    public MiscDataFile getMiscDataFile() {
        return miscDataFile;
    }

    public Settings getSettings() {
        return settings;
    }

}
