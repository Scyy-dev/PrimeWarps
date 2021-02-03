package me.Scyy.PrimeWarps.GUI.Sign;

import me.Scyy.PrimeWarps.GUI.Type.SignGUI;
import org.bukkit.block.Sign;

public class WorldSign {

    private final String worldName;

    private Sign sign;

    private SignGUI GUI;

    private boolean loaded;

    private boolean inUse;

    public WorldSign(String worldName) {
        this(worldName, null, null, false, false);
    }

    public WorldSign(String worldName, Sign sign, SignGUI GUI, boolean loaded, boolean inUse) {
        this.worldName = worldName;
        this.sign = sign;
        this.GUI = GUI;
        this.loaded = loaded;
        this.inUse = inUse;
    }

    public boolean isAvailable() {
        return sign != null && loaded && !inUse;
    }

    public String getWorldName() {
        return worldName;
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public SignGUI getGUI() {
        return GUI;
    }

    public void setGUI(SignGUI GUI) {
        this.GUI = GUI;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    @Override
    public String toString() {
        return "WorldSign{" +
                "worldName='" + worldName + '\'' +
                ", sign=" + sign +
                ", GUI=" + GUI +
                ", loaded=" + loaded +
                ", inUse=" + inUse +
                '}';
    }
}
