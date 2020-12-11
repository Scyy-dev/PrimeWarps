package me.Scyy.PrimeWarps.Config;

import me.Scyy.PrimeWarps.Plugin;

public class ConfigFileHandler {

    private final PlayerWarps playerWarps;
    private final PlayerMessenger playerMessenger;
    private final UserData userData;
    private final Settings settings;

    public ConfigFileHandler(Plugin plugin) {
        this.playerMessenger = new PlayerMessenger(plugin);
        this.playerWarps = new PlayerWarps(plugin);
        this.userData = new UserData(plugin);
        this.settings = new Settings(plugin);
    }

    public void reloadConfigs() {
        playerMessenger.reloadConfig();
        playerWarps.reloadConfig();
        userData.reloadConfig();
        settings.reloadConfig();
    }

    public PlayerMessenger getPlayerMessenger() {
        return playerMessenger;
    }

    public PlayerWarps getPlayerWarps() {
        return playerWarps;
    }

    public UserData getUserData() {
        return userData;
    }

    public Settings getSettings() {
        return settings;
    }

}
