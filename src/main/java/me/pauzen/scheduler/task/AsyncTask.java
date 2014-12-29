package me.pauzen.scheduler.task;

public abstract class AsyncTask extends Task {

    private Thread thread;

    public AsyncTask(Thread thread) {
        this.thread = thread;
    }

    /**
     * Does not use ticks.
     */
    @Override
    public void tick() {
    }

    @Override
    public void stop() {
        thread.stop();
    }

    @Override
    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Thread getThread() {
        return thread;
    }
}
