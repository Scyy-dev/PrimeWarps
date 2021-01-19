package me.Scyy.PrimeWarps.GUI.Type;

import me.Scyy.PrimeWarps.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This GUI system represents a template system for creating GUIs.<br>
 * Every time the player computes an action that requires a new GUI page with different handling,
 * it's recommended to produce a new GUI instance to keep interaction handling clean and concise.
 *
 * @param <T> The event responsible for handling interaction within this GUI
 */
public interface GUI<T extends Event> {

    /**
     * Handle the player manipulating the GUI through the Bukkit Event system
     * @param event the event to handle the GUI interaction
     * @return the new GUI
     */
    @NotNull GUI<?> handleInteraction(T event);

    /**
     * Open the GUI for a given player
     * @param player the player to view the GUI
     */
    void open(Player player);

    /**
     * Gets the GUI that was open before this one, or null if opened for the first time
     * @return the last GUI
     */
    @Nullable GUI<?> getLastGUI();

    /**
     * Gets the plugin this GUI is associated with. <br>
     * If you have a centralised data access system that isn't the plugin class,
     * it is recommended to replace the return type to that data system,
     * or add the ability to access such system through your plugin class
     * @return the plugin
     */
    @NotNull Plugin getPlugin();

    /**
     * Gets the player that will be or is currently viewing this GUI
     * @return the player
     */
    @NotNull Player getPlayer();

    /**
     * Determines if the GUI should be closed on the next tick instead of opening a new GUI
     * @return if the GUI should close on the next tick
     */
    boolean shouldClose();

}
