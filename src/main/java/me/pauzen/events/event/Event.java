package me.pauzen.events.event;

public abstract class Event {

    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlers() {
        return handlerList;
    }
}
