package me.pauzen.events.event;

public enum EventPriority implements Comparable<EventPriority> {

    FIRST(0),
    HIGHEST(1),
    HIGH(2),
    NORMAL(3),
    LOW(4),
    LOWEST(5),
    LAST(6);

    int position;

    EventPriority(int position) {
        this.position = position;
    }
}
