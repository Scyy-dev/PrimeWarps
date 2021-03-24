package me.Scyy.PrimeWarps.GUI.Sign;

import me.Scyy.PrimeWarps.GUI.Type.SignGUI;
import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class SignManager {

    private final NamespacedKey signDataKey;

    private final Map<Integer, WorldSign> signs;

    private int lastSignID = 0;

    public SignManager(Plugin plugin) {
        this.signDataKey = new NamespacedKey(plugin, "isSignGUI");
        this.signs = new HashMap<>();
    }

    public int createSign(SignGUI gui, String[] signText) {
        WorldSign sign = new WorldSign(gui, signText);
        this.signs.put(++lastSignID, sign);
        this.applySignTag(lastSignID);
        return lastSignID;
    }

    public void removeSign(int signID) {
        WorldSign sign = this.signs.get(signID);
        if (sign == null) return;
        sign.remove();
        this.signs.remove(signID);
    }

    public void applySignTag(int signID) {
        WorldSign sign = this.signs.get(signID);
        if (sign == null) return;
        sign.applyTag(signDataKey, signID);
    }

    public int getSignTag(Sign sign) {
        return WorldSign.getTag(sign, signDataKey);
    }

    public void openSign(Player player, int signID) {
        WorldSign sign = this.signs.get(signID);
        if (sign == null) throw new IllegalArgumentException("Cannot find sign with ID " + signID);
        player.openSign(sign.getSign());
    }

    public SignGUI getGUI(int signID) {
        WorldSign sign = this.signs.get(signID);
        if (sign == null) throw new IllegalArgumentException("Cannot find sign with ID " + signID);
        return sign.getGUI();
    }

    public static class WorldSign {

        private final Player player;

        private final SignGUI gui;

        private Sign sign;

        public WorldSign(SignGUI gui, String[] signText) {

            if (signText.length != 4) throw new IllegalArgumentException("Must be a String array of length 4");

            this.gui = gui;
            this.player = gui.getPlayer();
            this.sign = null;
            World world = player.getWorld();

            for (int i = player.getWorld().getMaxHeight(); i > 0; i--) {

                // Loop through each block the player stands in
                Location loc = new Location(world, player.getLocation().getBlockX(), i, player.getLocation().getBlockZ());

                // Find a block that is empty space
                Block block = world.getBlockAt(loc);
                if (block.getType() != Material.AIR) continue;

                // Place the block
                block.setType(Material.OAK_SIGN);
                Sign sign = (Sign) block.getState();

                this.sign = sign;

                // Customise the sign
                sign.setEditable(true);
                for (int signIndex = 0; i < 4; i++) {
                    sign.setLine(signIndex, signText[signIndex]);
                    // Update the sign with the new text
                    sign.update();
                }



                // Prevents plugin from more signs than needed
                break;

            }

        }

        public Player getPlayer() {
            return player;
        }

        public Sign getSign() {
            return sign;
        }

        public void remove() {
            this.sign.getBlock().setType(Material.AIR);
        }

        public void applyTag(NamespacedKey key, int tag) {
            this.sign.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, tag);
            this.sign.update();
        }

        public static int getTag(Sign sign, NamespacedKey key) {
            Integer integer = sign.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
            if (integer == null) return  -1;
            else return integer;
        }

        public SignGUI getGUI() {
            return gui;
        }
    }
}
