package me.Scyy.PrimeWarps;

import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    private BidRegister bidRegister;

    @Override
    public void onEnable() {
        this.bidRegister = new BidRegister();
    }
}
