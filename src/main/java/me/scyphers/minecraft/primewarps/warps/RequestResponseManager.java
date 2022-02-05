package me.scyphers.minecraft.primewarps.warps;

import java.util.List;
import java.util.UUID;

public interface RequestResponseManager {

    void scheduleResponse(UUID requester, WarpRequestResponse response);

    List<WarpRequestResponse> getResponses(UUID playerUUID);

    boolean hasResponse(UUID playerUUID);

    void clearResponses(UUID playerUUID);
}
