package me.Scyy.PrimeWarps.Warps.WarpSorters;

import me.Scyy.PrimeWarps.Warps.Warp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategorySorter implements WarpSorter {

    private final String category;

    private boolean activeSorter;

    public CategorySorter(String category) {
        this.category = category;
    }

    @Override
    public List<Warp> sort(Map<String, Warp> warps) {
        List<Warp> categoryWarps = new ArrayList<>();
        for (Warp warp : warps.values()) {
            if (warp.getCategory().equals(category)) categoryWarps.add(warp);
        }
        return categoryWarps;
    }

    @Override
    public String getActiveText() {
        return "&7> By category (" + category + ")";
    }

    @Override
    public int listPosition() {
        return 1;
    }

    public String getCategory() {
        return category;
    }
}
