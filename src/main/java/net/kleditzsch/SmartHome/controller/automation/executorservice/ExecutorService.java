package net.kleditzsch.SmartHome.controller.automation.executorservice;

import net.kleditzsch.SmartHome.controller.automation.executorservice.command.Interface.Command;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.StopCommand;

import java.util.concurrent.*;

/**
 * Service zum ausführen von Schaltbefehlen
 */
public class ExecutorService {

    /**
     * Warteschlange
     */
    private BlockingQueue<Command> queue;

    /**
     * Thread Pool
     */
    private java.util.concurrent.ExecutorService executor;

    /**
     * markierung zum stoppen des Service
     */
    private boolean stopFlag = false;

    /**
     * startet den Service
     */
    public void startService() {

        //Threadpool erzeugen
        executor = Executors.newFixedThreadPool(50);

        //Scheduler starten
        queue = new LinkedBlockingQueue<>();
        executor.execute(new ExecutorScheduler(queue, executor));
    }

    /**
     * nimmt die Schaltbefehle an und verwaltet Sie in der Warteschlange
     *
     * @param command Schaltbefehl
     * @return Erfolgsmeldung
     */
    public boolean putCommand(Command command) {

        if(queue != null) {

            if (!stopFlag) {

                return queue.offer(command);
            }
            return false;
        }
        throw new IllegalStateException("Der Service ist nicht initalisiert");
    }

    /**
     * beendet den Service
     */
    public void stopService() {

        if (queue != null && executor != null) {

            try {

                stopFlag = true;
                queue.put(new StopCommand());
                executor.shutdown();
                executor.awaitTermination(1, TimeUnit.SECONDS);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        }
    }
}
