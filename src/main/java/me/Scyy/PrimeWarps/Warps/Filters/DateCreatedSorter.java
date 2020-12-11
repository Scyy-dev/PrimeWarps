package me.Scyy.PrimeWarps.Warps.Filters;

import me.Scyy.PrimeWarps.Warps.Warp;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DateCreatedSorter implements WarpSorter {

    @Override
    public List<Warp> sort(Map<String, Warp> warps) {
        return warps.values().stream().sorted((Comparator.comparing(Warp::getDateCreated))).collect(Collectors.toList());
    }
}
