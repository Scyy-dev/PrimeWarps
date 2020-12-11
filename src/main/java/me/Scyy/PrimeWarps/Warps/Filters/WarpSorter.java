package me.Scyy.PrimeWarps.Warps.Filters;

import me.Scyy.PrimeWarps.Warps.Warp;

import java.util.List;
import java.util.Map;

public interface WarpSorter {

    List<Warp> sort(Map<String, Warp> warps);
}
