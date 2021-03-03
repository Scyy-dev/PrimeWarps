package me.Scyy.PrimeWarps.Warps;

import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.entity.Player;

import java.util.*;

public class WarpRegister {

    private final Map<String, Warp> warps;

    private final Map<String, WarpRequest> warpRequests;

    private final Map<UUID, Set<WarpRequestHandler>> requestHandlers;

    private final WarpRequestScheduler requestScheduler;

    public WarpRegister(Plugin plugin, Map<String, Warp> warps, Map<String, WarpRequest> warpRequests, Map<UUID, Set<WarpRequestHandler>> requestHandlers) {
        this.warps = warps;
        this.warpRequests = warpRequests;
        this.requestHandlers = requestHandlers;
        this.requestScheduler = new WarpRequestScheduler(plugin);
    }

    public void updateWarpInactivity(int days) {
        for (Warp warp : warps.values()) {
            if (warp.testInactivity(days)) warp.setInactive(true);
        }
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

    public void updateWarp(Warp oldWarp, Warp newWarp) {
        warps.remove(oldWarp.getName());
        warps.put((newWarp.getName()), newWarp);
    }

    public void forceNewWeek() {
        for (Warp warp : warps.values()) {
            warp.newWeek();
        }
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

    public Map<UUID, Set<WarpRequestHandler>> getRequestHandlerMap() {
        return requestHandlers;
    }

    public Set<WarpRequestHandler> getRequestHandlerList(UUID owner) {
        return requestHandlers.get(owner);
    }

    /**
     * Adds a warp handler to the request handler collection
     * @param owner owner of the request handler
     * @param warpRequestHandler warp handler to add
     */
    public void addWarpHandler(UUID owner, WarpRequestHandler warpRequestHandler) {
        Set<WarpRequestHandler> handlers = requestHandlers.get(owner);
        if (handlers == null) {
            requestHandlers.put(owner, new LinkedHashSet<>());
            requestHandlers.get(owner).add(warpRequestHandler);
            return;
        }
        handlers.add(warpRequestHandler);

    }

    /**
     * Removes a warp handler from the request handler collection
     * @param handler owner of the request handler
     */
    public void removeWarpHandler(WarpRequestHandler handler) {
        if (!requestHandlers.containsKey(handler.getOwner())) return;
        requestHandlers.get(handler.getOwner()).remove(handler);
    }

    public void filterHandlers(Player player) {
        if (requestHandlers.get(player.getUniqueId()) == null) return;
        requestHandlers.get(player.getUniqueId()).removeIf((handler -> !handler.isRefundWarpShards() && handler.getRequestMessage() == null));
    }

    public WarpRequestScheduler getRequestScheduler() {
        return requestScheduler;
    }
}
