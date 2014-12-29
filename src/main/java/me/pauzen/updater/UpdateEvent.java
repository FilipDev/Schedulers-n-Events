package me.pauzen.updater;


import me.pauzen.events.event.Event;

public class UpdateEvent extends Event {

    private UpdateType updateType;

    public UpdateEvent(UpdateType updateType) {
        this.updateType = updateType;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

}
