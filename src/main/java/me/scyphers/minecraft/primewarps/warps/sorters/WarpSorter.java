package me.scyphers.minecraft.primewarps.warps.sorters;

import me.scyphers.minecraft.primewarps.warps.Warp;

import java.util.List;
import java.util.Map;

public interface WarpSorter {

    List<Warp> sort(Map<String, Warp> warps);

    String getActiveText();

    int listPosition();

}
