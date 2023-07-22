package me.scyphers.minecraft.primewarps.gui.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.scyphers.minecraft.primewarps.PrimeWarps;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

public class ChatManager implements Listener {

    private static final Map<Class<? extends Event>, BiFunction<Player, String, ? extends Event>> events = new HashMap<>();

    private static final PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();

    private final Map<UUID, ChatRecord<?>> activeChats = new HashMap<>();

    private final PrimeWarps plugin;

    private final Executor serverThread;

    public ChatManager(PrimeWarps plugin) {
        this.plugin = plugin;
        this.serverThread = plugin.serverThread();
    }

    public ChatRecord<?> getChat(UUID uuid) {
        return activeChats.getOrDefault(uuid, ChatRecord.INVALID);
    }

    public <T extends Event> void registerChat(UUID player, int timeOutInSeconds, Runnable onTimeOut, Class<T> clazz) {
        long timeOut = System.currentTimeMillis() + timeOutInSeconds;
        int taskID = plugin.getServer().getScheduler().runTaskLater(plugin, onTimeOut, timeOut).getTaskId();
        this.activeChats.put(player, new ChatRecord<T>(taskID, clazz));
    }

    public void removeChat(UUID player) {
        // Check for a valid chat
        ChatRecord<?> record = this.getChat(player);
        if (ChatRecord.isInvalid(record)) return; // No chat to remove!

        // Purge the chat from internal and Minecraft scheduler
        plugin.getServer().getScheduler().cancelTask(record.taskID);
        activeChats.remove(player);
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncChatEvent event) {

        // Check if a player is actively waiting on a chat action
        ChatRecord<?> record = this.getChat(event.getPlayer().getUniqueId());
        if (ChatManager.ChatRecord.isInvalid(record)) return;

        // Convert chat message to raw text
        String rawText = serializer.serialize(event.originalMessage());

        // Process the chat message
        if (!events.containsKey(record.clazz)) {
            plugin.getLogger().warning("Unrecognised event: " + record.clazz.getSimpleName());
        }

        // Convert the normal chat event to an event registered by
        BiFunction<Player, String, ? extends Event> eventFunction = events.get(record.clazz);
        Event chatEvent = eventFunction.apply(event.getPlayer(), rawText);

        // Call the event on the server thread
        serverThread.execute(() -> plugin.getServer().getPluginManager().callEvent(chatEvent));
    }

    public synchronized static <T extends Event> void registerChatEvent(Class<T> clazz, BiFunction<Player, String, ? extends T> supplier) {
        events.put(clazz, supplier);
    }

    public static record ChatRecord<T extends Event>(int taskID, Class<T> clazz) {

        private static final ChatRecord<?> INVALID = new ChatRecord<>(-1, Event.class);

        public static boolean isInvalid(ChatRecord<?> record) {
            return record.taskID == -1;
        }

    }

}
