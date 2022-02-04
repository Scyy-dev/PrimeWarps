package me.scyphers.minecraft.primewarps.warps;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface WarpRegister {

    boolean warpExists(String warpName);

    Collection<Warp> getAllWarps();

    void removeWarp(String warpName);

    List<Warp> getIslandWarps(UUID islandUUID);

}
