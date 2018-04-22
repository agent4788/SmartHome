package net.kleditzsch.SmartHome.app.automation;

import net.kleditzsch.SmartHome.controller.automation.avmservice.AvmDataUpdateService;
import net.kleditzsch.SmartHome.controller.automation.avmservice.AvmEditor;
import net.kleditzsch.SmartHome.controller.automation.executorservice.ExecutorService;
import net.kleditzsch.SmartHome.controller.automation.tplinkservice.TpLinkUpdateService;
import net.kleditzsch.SmartHome.model.automation.editor.RoomEditor;
import net.kleditzsch.SmartHome.model.automation.editor.SensorEditor;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchServerEditor;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutomationAppliaction {

    /**
     * Scheduler
     */
    private volatile ScheduledExecutorService timerExecutor;

    /**
     * Sensor Editor
     */
    private volatile SensorEditor sensorEditor;

    /**
     * Schaltbare Elemente Editor
     */
    private volatile SwitchableEditor switchableEditor;

    /**
     * Schaltserver Editor
     */
    private volatile SwitchServerEditor switchServerEditor;

    /**
     * Raum Editor
     */
    private volatile RoomEditor roomEditor;

    /**
     * Ausführungsservice
     */
    private volatile ExecutorService executorService;

    /**
     * AVM Editor
     */
    private volatile AvmEditor avmEditor;

    /**
     * Initlisiert die Anwendungsdaten
     */
    public void init() {

        sensorEditor = new SensorEditor();
        sensorEditor.load();

        switchableEditor = new SwitchableEditor();
        switchableEditor.load();

        switchServerEditor = new SwitchServerEditor();
        switchServerEditor.load();

        roomEditor = new RoomEditor();
        roomEditor.load();

        avmEditor = new AvmEditor();
        avmEditor.load();
    }

    /**
     * gibt den Sensor-Editor zurück
     *
     * @return Sensor-Editor
     */
    public SensorEditor getSensorEditor() {
        return sensorEditor;
    }

    /**
     * gibt den Schaltelemente-Editor zurück
     *
     * @return Schaltelemente-Editor
     */
    public SwitchableEditor getSwitchableEditor() {
        return switchableEditor;
    }

    /**
     * gibt den Schaltserver-Editor zurück
     *
     * @return Schaltserver-Editor
     */
    public SwitchServerEditor getSwitchServerEditor() {
        return switchServerEditor;
    }

    /**
     * gibt den AVM Editor zurück
     *
     * @return AVM Editor
     */
    public AvmEditor getAvmEditor() {
        return avmEditor;
    }

    /**
     * gibt den Ausführungsservice zurück
     *
     * @return Ausführungsservice
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * startet die Anwendung
     */
    public void start() {

        //Scheduler Threadpool erzeugen
        timerExecutor = Executors.newScheduledThreadPool(2);

        //Executor starten
        executorService = new ExecutorService();
        executorService.startService();

        //AVM Update Task starten
        if(avmEditor.isActive()) {

            timerExecutor.scheduleAtFixedRate(new AvmDataUpdateService(), 30, 15, TimeUnit.SECONDS);
        }

        //TP Link Update Task starten
        timerExecutor.scheduleAtFixedRate(new TpLinkUpdateService(), 10, 15, TimeUnit.SECONDS);


    }

    /**
     * Speichert alle Anwendungsdaten
     */
    public void dump() {

        sensorEditor.dump();
        switchableEditor.dump();
        switchServerEditor.dump();
        roomEditor.dump();
    }

    /**
     * Beendet die Automatisierungsanwendung
     */
    public void stop() {

        timerExecutor.shutdown();
        executorService.stopService();
    }
}
