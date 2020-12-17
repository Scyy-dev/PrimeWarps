package me.Scyy.PrimeWarps.Warps;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InactiveMessageScheduler {

    private final Map<UUID, List<String>> inactiveMessages;

    public InactiveMessageScheduler(Map<UUID, List<String>> inactiveMessages) {
        this.inactiveMessages = inactiveMessages;
    }

    public Map<UUID, List<String>> getInactiveMessages() {
        return inactiveMessages;
    }

    public List<String> getMessages(UUID uuid) {
        return inactiveMessages.get(uuid);
    }

    public void addMessage(UUID uuid, String message) {
        List<String> messages = inactiveMessages.get(uuid);
        if (messages == null) {
            List<String> newMessages = new LinkedList<>();
            newMessages.add(message);
            inactiveMessages.put(uuid, newMessages);
        } else {
            messages.add(message);
        }

    }

    public void removeMessages(UUID uuid) {
        this.inactiveMessages.remove(uuid);
    }

}
