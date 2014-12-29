package me.pauzen.scheduler.task;

import me.pauzen.scheduler.Scheduler;

public abstract class SyncTask extends Task {

    private long last = System.currentTimeMillis();
    private long delay;

    public SyncTask(long delay) {
        this.delay = delay;
    }

    /**
     * Runs every single tick.
     */
    public SyncTask() {
        this.delay = 0;
    }

    public boolean elapsed() {
        boolean elapsed = currentTime() - last >= delay;
        if (elapsed) last = System.currentTimeMillis();
        return elapsed;
    }

    private static long currentTime() {
        return System.currentTimeMillis();
    }

    public void tick() {
        if (elapsed()) {
            if (currentTime() - sleepStart >= sleepTime && !isCancelled()) {
                this.run();
            }
        }
    }

    @Override
    public void stop() {
        Scheduler.getScheduler().stop(this);
        setCancelled(true);
    }

    @Override
    public void start() {
        setCancelled(false);
    }

    private long sleepStart;
    private long sleepTime;

    @Override
    public void sleep(long millis) {
        sleepStart = System.currentTimeMillis();
        sleepTime = millis;
    }
}
