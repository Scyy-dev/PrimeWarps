package me.Scyy.PrimeWarps.Warps.WarpSorters;

import me.Scyy.PrimeWarps.Warps.Warp;

import java.util.List;
import java.util.Map;

public interface WarpSorter {

    List<Warp> sort(Map<String, Warp> warps);

    String getActiveText();

    int listPosition();

}
