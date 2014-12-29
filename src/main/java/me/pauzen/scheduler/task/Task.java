package me.pauzen.scheduler.task;

public abstract class Task implements Runnable {

    private volatile boolean cancelled = false;

    public void pause() {
        setCancelled(true);
    }

    public abstract void tick();

    public abstract void stop();

    public void resume() {
        setCancelled(false);
    }

    public abstract void start();

    public abstract void sleep(long millis);

    public synchronized boolean isCancelled() {
        return cancelled;
    }

    public synchronized void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
