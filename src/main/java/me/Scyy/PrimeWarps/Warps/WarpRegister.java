package me.Scyy.PrimeWarps.Warps;

import java.util.Locale;
import java.util.Map;

public class WarpRegister {

    private final Map<String, Warp> warps;

    private final Map<String, WarpRequest> warpRequests;

    public WarpRegister(Map<String, Warp> warps, Map<String, WarpRequest> warpRequests) {
        this.warps = warps;
        this.warpRequests = warpRequests;
    }

    public Map<String, Warp> getWarps() {
        return warps;
    }

    public Warp getWarp(String name) {
        return warps.get(name.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Adds a warp to the warps collection
     * @param name name of the warp
     * @param warp warp to add
     * @return if the warp was added
     */
    public boolean addWarp(String name, Warp warp) {
        String formatName = name.toLowerCase(Locale.ENGLISH);
        if (warps.containsKey(formatName)) return false;
        else {
            warps.put(formatName, warp);
            return true;
        }
    }

    /**
     * Removes a warp from the warps collection
     * @param name name of the warp
     * @return if the warp was successfully removed
     */
    public boolean removeWarp(String name) {
        String formatName = name.toLowerCase(Locale.ENGLISH);
        if (!warps.containsKey(formatName)) return false;
        else {
            warps.remove(formatName);
            return true;
        }
    }

    public boolean warpExists(String name) {
        String formatName = name.toLowerCase(Locale.ENGLISH);
        return warps.containsKey(formatName);
    }

    public Map<String, WarpRequest> getWarpRequests() {
        return warpRequests;
    }

    public WarpRequest getWarpRequest(String name) {
        return warpRequests.get(name.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Adds a warpRequest to the warps collection
     * @param name name of the warpRequest
     * @param warpRequest warpRequest to add
     * @return if the warpRequest was added
     */
    public boolean addWarpRequest(String name, WarpRequest warpRequest) {
        String formatName = name.toLowerCase(Locale.ENGLISH);
        if (warpRequests.containsKey(formatName)) return false;
        else {
            warpRequests.put(formatName, warpRequest);
            return true;
        }
    }

    /**
     * Removes a warp from the warps collection
     * @param name name of the warp
     * @return if the warp was successfully removed
     */
    public boolean removeWarpRequest(String name) {
        String formatName = name.toLowerCase(Locale.ENGLISH);
        if (!warpRequests.containsKey(formatName)) return false;
        else {
            warpRequests.remove(formatName);
            return true;
        }
    }

    public boolean warpRequestExists(String name) {
        String formatName = name.toLowerCase(Locale.ENGLISH);
        return warpRequests.containsKey(formatName);
    }
}
