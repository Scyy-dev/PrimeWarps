package me.scyphers.minecraft.primewarps.warps;

public interface WarpRequestRegister {

    boolean warpRequestExists(String warpName);

    boolean addRequest(String warpName, WarpRequest request);

}
