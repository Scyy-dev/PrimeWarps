package me.scyphers.minecraft.primewarps.warps;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface WarpRequestRegister {

    boolean warpRequestExists(String warpName);

    boolean addRequest(String warpName, WarpRequest request);

    Collection<WarpRequest> getRequests();

    void removeWarpRequest(String warpName);

    List<WarpRequest> getRequests(UUID islandUUID);
}
