package me.pauzen.events.event;

import me.pauzen.events.event.Event;

public abstract class CancellableEvent extends Event {

    private boolean cancelled = false;

    public void setCancelled(boolean cancelled) {
        this.cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
