package me.scyphers.minecraft.primewarps.external;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import net.geekxboy.skyblock.Skyblock;
import net.geekxboy.skyblock.enums.IslandRole;
import net.geekxboy.skyblock.managers.Island;
import net.geekxboy.skyblock.managers.SkyblockPlayer;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class SkyblockManager {

    private final PrimeWarps plugin;

    private final boolean pluginLoaded;

    private final Map<String, IslandRole> roleMap = Map.of(
            "OWNER", IslandRole.OWNER,
            "COOWNER", IslandRole.COOWNER,
            "TRUSTED", IslandRole.TRUSTED,
            "MEMBER", IslandRole.MEMBER
    );

    public SkyblockManager(PrimeWarps plugin) {
        this.plugin = plugin;

        try {
            Skyblock skyblock = Skyblock.getInstance();
        } catch (NoClassDefFoundError error) {
            this.pluginLoaded = false;
            return;
        }

        this.pluginLoaded = true;

    }

    public boolean isPluginLoaded() {
        return pluginLoaded;
    }

    public boolean isWithinPlayerIsland(UUID playerUUID, Location location) {
        if (!pluginLoaded) return true;

        // Get the island from the location
        Island island = Skyblock.getInstance().getIslandManager().getIsland(location);
        if (island == null) return false;

        // Check if this player is a member of the island
        return island.registeredPlayer().contains(playerUUID);
    }

    public boolean hasAtLeast(UUID playerUUID, String islandRole) {
        if (!pluginLoaded) return true;

        Island island = this.getIslandFromPlayer(playerUUID);
        if (island == null) return false;

        SkyblockPlayer player = island.getPlayer(playerUUID);
        if (player == null) return false;

        IslandRole playerRole = player.getRole();

        IslandRole role = roleMap.get(islandRole.toUpperCase(Locale.ROOT));
        if (role == null) return false;

        return playerRole.ordinal() <= role.ordinal();

    }

    public UUID getIslandUUID(UUID playerUUID) {
        if (!pluginLoaded) return null;

        return this.getIslandFromPlayer(playerUUID).getUuid();
    }

    public Island getIslandFromPlayer(UUID playerUUID) {
        if (!pluginLoaded) return null;

        // Try get the player if the player is online
        Player player = plugin.getServer().getPlayer(playerUUID);
        if (player != null) return Skyblock.getInstance().getIslandManager().getIsland(player);

        // Get the offline player instead
        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(playerUUID);
        return Skyblock.getInstance().getIslandManager().getIsland(offlinePlayer);

    }

    public Island getIslandFromUUID(UUID islandUUID) {
        if (!pluginLoaded) return null;

        return Skyblock.getInstance().getIslandManager().getIsland(islandUUID);
    }

    public UUID getIslandOwner(UUID islandUUID) {
        if (!pluginLoaded) return null;

        Island island = this.getIslandFromUUID(islandUUID);
        if (island == null) return null;

        for (UUID playerUUID : island.getPlayers().keySet()) {

            SkyblockPlayer player = island.getPlayers().get(playerUUID);
            if (player.getRole() == IslandRole.OWNER) return playerUUID;

        }

        // Impossible for an island not to have an owner, and we cannot handle it differently
        throw new IllegalStateException("Island does not have an island owner");

    }

}
