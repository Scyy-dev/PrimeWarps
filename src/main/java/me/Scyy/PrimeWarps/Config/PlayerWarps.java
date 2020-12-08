package me.Scyy.PrimeWarps.Config;

import me.Scyy.PrimeWarps.Plugin;
import me.Scyy.PrimeWarps.Warps.Warp;
import me.Scyy.PrimeWarps.Warps.WarpRequest;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerWarps extends ConfigFile {

    public PlayerWarps(Plugin plugin) {
        super(plugin, "warps.yml");
    }

    public Map<String, Warp> loadWarps() {
        return new LinkedHashMap<>();
    }

    public void saveWarps(Map<String, Warp> warps) {

    }

    public Map<String, WarpRequest> loadWarpRequests() {
        return new LinkedHashMap<>();
    }

    public void saveWarpRequests(Map<String, WarpRequest> warpRequests) {}


}
