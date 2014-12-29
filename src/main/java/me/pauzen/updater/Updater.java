package me.pauzen.updater;

import me.pauzen.events.Caller;
import me.pauzen.scheduler.Scheduler;
import me.pauzen.scheduler.task.SyncTask;

public class Updater {

    private static Updater updater;

    public static Updater getUpdater() {
        if (updater == null) updater = new Updater(Caller.getCaller());
        return updater;
    }

    public Updater(Caller caller) {

        Scheduler.getScheduler().scheduleSyncRepeatingTask(new SyncTask(50L) {
            @Override
            public void run() {
                for (UpdateType updateType : UpdateType.values()) {
                    if (updateType.elapsed()) {
                        caller.callEvent(new UpdateEvent(updateType));
                    }
                }
            }
        });
    }

}
