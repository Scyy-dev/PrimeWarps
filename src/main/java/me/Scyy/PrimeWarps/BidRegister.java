package me.Scyy.PrimeWarps;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BidRegister {

    private final Map<UUID, Double> bids;

    public BidRegister() {
        this.bids = new HashMap<>();
    }

    public Map<UUID, Double> getBids() {
        return bids;
    }

    public void addBid(UUID uuid, Double bid) {
        this.bids.put(uuid, bid);
    }

    public UUID getHighest() {
        double highestBid = 0;
        UUID highestBidder = null;
        for (UUID uuid : bids.keySet()) {
            if (bids.get(uuid) > highestBid) {
                highestBidder = uuid;
                highestBid = bids.get(uuid);
            }
        }
        return highestBidder;
    }

}
