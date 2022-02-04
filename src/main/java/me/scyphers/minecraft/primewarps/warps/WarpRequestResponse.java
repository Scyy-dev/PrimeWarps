package me.scyphers.minecraft.primewarps.warps;

import java.util.UUID;

public record WarpRequestResponse(String warpName, UUID islandUUID, UUID warpRequester, boolean refundShards) {
}
