import me.pauzen.events.Caller;
import me.pauzen.events.Listener;
import me.pauzen.events.event.EventHandler;
import me.pauzen.scheduler.Scheduler;
import me.pauzen.scheduler.task.SyncTask;
import me.pauzen.updater.UpdateEvent;
import me.pauzen.updater.Updater;

public class Main implements Listener {

    public static void main(String[] args) {

        final Main main = new Main();

        Caller.getCaller().registerListener(main);
        Updater.getUpdater();

        Thread thread = new Thread(() -> {
            Scheduler scheduler = Scheduler.getScheduler();

            Caller caller = new Caller();
            caller.registerListener(main);
            caller.callEvent(new TestEvent());
            SyncTask syncTask = new SyncTask(1000L) {
                @Override
                public void run() {
                    caller.callEvent(new TestEvent());
                }
            };
            scheduler.scheduleSyncRepeatingTask(syncTask);
            ticker(scheduler);
        });
        thread.start();
    }

    public static void ticker(Scheduler scheduler) {
        while (true) {
            scheduler.tick();
            try {
                Thread.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void up(UpdateEvent event) {
        System.out.println(event.getUpdateType());
    }

    @EventHandler
    public void t(TestEvent event) {
        System.out.println(System.currentTimeMillis());
    }
}
