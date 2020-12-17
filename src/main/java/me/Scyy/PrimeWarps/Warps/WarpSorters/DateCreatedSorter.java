package me.Scyy.PrimeWarps.Warps.WarpSorters;

import me.Scyy.PrimeWarps.Warps.Warp;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DateCreatedSorter implements WarpSorter {
    @Override
    public List<Warp> sort(Map<String, Warp> warps) {
        return warps.values().stream().sorted(Comparator.comparing(Warp::getDateCreated).reversed()).collect(Collectors.toList());
    }

    @Override
    public String getActiveText() {
        return "&7> By date created";
    }

    @Override
    public int listPosition() {
        return 0;
    }

}
