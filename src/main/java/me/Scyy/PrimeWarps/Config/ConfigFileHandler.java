package me.Scyy.PrimeWarps.Config;

import me.Scyy.PrimeWarps.Plugin;

public class ConfigFileHandler {

    private final PlayerWarps playerWarps;
    private final PlayerMessenger playerMessenger;
    private final MiscDataStorage miscDataStorage;
    private final Settings settings;

    public ConfigFileHandler(Plugin plugin) {
        this.playerMessenger = new PlayerMessenger(plugin);
        this.playerWarps = new PlayerWarps(plugin);
        this.miscDataStorage = new MiscDataStorage(plugin);
        this.settings = new Settings(plugin);
    }

    public void reloadConfigs() {
        playerMessenger.reloadConfig();
        playerWarps.reloadConfig();
        miscDataStorage.reloadConfig();
        settings.reloadConfig();
    }

    public PlayerMessenger getPlayerMessenger() {
        return playerMessenger;
    }

    public PlayerWarps getPlayerWarps() {
        return playerWarps;
    }

    public MiscDataStorage getMiscDataStorage() {
        return miscDataStorage;
    }

    public Settings getSettings() {
        return settings;
    }

}
