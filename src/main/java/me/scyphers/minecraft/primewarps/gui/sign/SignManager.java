package me.scyphers.minecraft.primewarps.gui.sign;

import me.scyphers.minecraft.primewarps.PrimeWarps;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class SignManager {

    private final NamespacedKey signDataKey;

    private final Map<Integer, SignData> signs;

    private int lastSignID = 0;

    public SignManager(PrimeWarps plugin) {
        this.signDataKey = new NamespacedKey(plugin, "signGUI");
        this.signs = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(new SignListener(this), plugin);
    }

    public int getSignTag(Sign sign) {
        return sign.getPersistentDataContainer().getOrDefault(signDataKey, PersistentDataType.INTEGER, -1);
    }

    public Sign placeSign(int signID, Location startPosition, String[] signText) {

        int x = startPosition.getBlockX();
        int z = startPosition.getBlockZ();

        for (int y = startPosition.getWorld().getMaxHeight(); y > 0; y--) {

            // Find a block that is empty space
            Block block = startPosition.getWorld().getBlockAt(x, y, z);
            if (block.getType() != Material.AIR) continue;

            // Place the block
            block.setType(Material.OAK_SIGN);
            Sign sign = (Sign) block.getState();

            // Customise the sign
            sign.setEditable(true);
            for (int signIndex = 0; signIndex < 4; signIndex++) {
                sign.setLine(signIndex, signText[signIndex]);
            }

            // Add the data tag to the block
            sign.getPersistentDataContainer().set(signDataKey, PersistentDataType.INTEGER, signID);

            // Update the sign
            sign.update();

            // Sign successfully placed
            return sign;

        }

        // No valid block to place sign found, return false
        return null;
    }

    public int initSign(SignGUI gui, String[] text) {
        if (text.length != 4) {
            throw new IllegalArgumentException("must have 4 lines for sign");
        }

        int nextID = this.getNextID();
        Location location = gui.getPlayer().getLocation();

        Sign placedSign = placeSign(nextID, location, text);

        if (placedSign != null) {
            signs.put(nextID, new SignData(gui, placedSign));
            return nextID;
        } else {
            return -1;
        }

    }

    public boolean isValidSign(int signID) {
        return signID != -1 && signs.containsKey(signID);
    }

    public SignGUI getGUI(int signID) {
        if (!isValidSign(signID)) return null;
        return signs.get(signID).gui;
    }

    public Sign getSign(int signID) {
        if (!isValidSign(signID)) return null;
        return signs.get(signID).sign;
    }

    public void removeSign(int signID) {
        if (signs.containsKey(signID)) {

            // Remove the sign from the game world
            Sign sign = this.signs.get(signID).sign;
            Block block = sign.getLocation().getBlock();
            block.setType(Material.AIR);

            // Remove the sign from the cache of sign GUIs
            this.signs.remove(signID);
            System.out.println("Removed sign with ID " + signID);
        }
    }

    private int getNextID() {
        return lastSignID++;
    }

    private static record SignData(SignGUI gui, Sign sign) {}

}
