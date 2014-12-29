package me.pauzen.scheduler;

import me.pauzen.scheduler.task.AsyncTask;
import me.pauzen.scheduler.task.SyncTask;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {

    private        Thread    owner;
    private static Scheduler scheduler;

    public Scheduler(Thread owner) {
        this.owner = owner;
        scheduler = this;
    }

    private List<SyncTask> syncTaskList = new ArrayList<>();

    public boolean tick() {
        if (syncTaskList.isEmpty()) {
            owner.stop();
            return false;
        }
        syncTaskList.forEach(SyncTask::tick);
        return true;
    }

    public void stop(SyncTask syncTask) {
        int index = syncTaskList.indexOf(syncTask);
        syncTaskList.remove(index);
    }

    public static Scheduler getScheduler() {
        if (scheduler == null) new Scheduler(Thread.currentThread());
        return scheduler;
    }

    public void runAsyncTask(AsyncTask task) {
        task.start();
    }

    public void scheduleSyncRepeatingTask(SyncTask syncTask) {
        syncTaskList.add(syncTask);
        syncTask.start();
    }

    public void scheduleAsyncRepeatingTask(AsyncTask asyncTask, long delay, long initial) {
        AsyncTask newTask = new AsyncTask(asyncTask.getThread()) {
            @Override
            public void run() {
                try {
                    Thread.sleep(initial);
                    while (!this.isCancelled()) {
                        asyncTask.getThread().run();
                        Thread.sleep(delay);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        runAsyncTask(newTask);
    }

}
