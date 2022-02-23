package me.scyphers.minecraft.primewarps;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class AsyncBridge {

    private final PrimeWarps plugin;

    private final int taskID;

    private final BlockingQueue<Consumer<PrimeWarps>> syncTasks;

    public AsyncBridge(PrimeWarps plugin) {
        this.plugin = plugin;

        this.syncTasks = new ArrayBlockingQueue<>(20);

        this.taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            if (syncTasks.isEmpty()) return;

            // Move all tasks into the list
            List<Consumer<PrimeWarps>> tasks = new ArrayList<>();
            syncTasks.drainTo(tasks);

            tasks.forEach(consumer -> consumer.accept(plugin));

        }, 20, 1);

    }

    public synchronized void addTask(Consumer<PrimeWarps> task) {
        try {
            this.syncTasks.put(task);
        } catch (Exception e) {
            plugin.getSLF4JLogger().error("Unable to accept async task", e);
        }

    }

    public void endTasks() {
        plugin.getServer().getScheduler().cancelTask(taskID);
    }

}
