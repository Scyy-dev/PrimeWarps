package me.scyphers.minecraft.primewarps.warps;

import org.bukkit.Location;

import java.time.Instant;
import java.util.UUID;

public record WarpRequest(String name, UUID islandUUID, Location location, String category, Instant dateCreated) { }
