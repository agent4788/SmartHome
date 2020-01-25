package net.kleditzsch.SmartHome.app.automation;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.app.SubApplication;
import net.kleditzsch.SmartHome.controller.automation.avmservice.AvmDataUpdateService;
import net.kleditzsch.SmartHome.controller.automation.avmservice.AvmEditor;
import net.kleditzsch.SmartHome.controller.automation.executorservice.ExecutorService;
import net.kleditzsch.SmartHome.controller.automation.mqttservice.MqttService;
import net.kleditzsch.SmartHome.controller.automation.sensorservice.LiveBitUpdateService;
import net.kleditzsch.SmartHome.controller.automation.sensorservice.UserAtHomeUpdateService;
import net.kleditzsch.SmartHome.controller.automation.sensorservice.VirtualSensorUpdateService;
import net.kleditzsch.SmartHome.controller.automation.switchtimerservice.SwitchTimerService;
import net.kleditzsch.SmartHome.controller.automation.tplinkservice.TpLinkUpdateService;
import net.kleditzsch.SmartHome.model.automation.editor.*;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.view.automation.admin.*;
import net.kleditzsch.SmartHome.view.automation.admin.device.*;
import net.kleditzsch.SmartHome.view.automation.admin.room.*;
import net.kleditzsch.SmartHome.view.automation.admin.sensorvalues.*;
import net.kleditzsch.SmartHome.view.automation.admin.settings.AutomationSettingsServlet;
import net.kleditzsch.SmartHome.view.automation.admin.switchserver.AutomationSwitchServerDeleteServlet;
import net.kleditzsch.SmartHome.view.automation.admin.switchserver.AutomationSwitchServerFormServlet;
import net.kleditzsch.SmartHome.view.automation.admin.switchserver.AutomationSwitchServerListServlet;
import net.kleditzsch.SmartHome.view.automation.admin.timer.AutomationTimerDeleteServlet;
import net.kleditzsch.SmartHome.view.automation.admin.timer.AutomationTimerFormServlet;
import net.kleditzsch.SmartHome.view.automation.admin.timer.AutomationTimerListServlet;
import net.kleditzsch.SmartHome.view.automation.user.*;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Hauptklasse der Automatisierungsanwendung
 */
public class AutomationAppliaction implements SubApplication {

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
     * Schalt Timer Editor
     */
    private volatile SwitchTimerEditor switchTimerEditor;

    /**
     * Ausführungsservice
     */
    private volatile ExecutorService executorService;

    /**
     * MQTT Service
     */
    private volatile MqttService mqttService;

    /**
     * Threadpool mit Zeitsteuerung
     */
    private volatile ScheduledExecutorService timerExecutor;

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

        switchTimerEditor = new SwitchTimerEditor();
        switchTimerEditor.load();

        avmEditor = new AvmEditor();
        avmEditor.load();

        mqttService = new MqttService();
    }

    /**
     * initalisiert die Webinhalte der Anwendung
     *
     * @param contextHandler Context Handler
     */
    public void initWebContext(ServletContextHandler contextHandler) {

        contextHandler.addServlet(AutomationIndexServlet.class, "/automation/");
        contextHandler.addServlet(AutomationIndexServlet.class, "/automation/index");
        contextHandler.addServlet(AutomationRoomServlet.class, "/automation/room");
        contextHandler.addServlet(AutomationSwitchServlet.class, "/automation/switch");
        contextHandler.addServlet(AutomationSyncServlet.class, "/automation/sync");
        contextHandler.addServlet(AutomationSseSyncServlet.class, "/automation/ssesync");

        contextHandler.addServlet(AutomationAdminIndexServlet.class, "/automation/admin/");
        contextHandler.addServlet(AutomationAdminIndexServlet.class, "/automation/admin/index");
        contextHandler.addServlet(AutomationSettingsServlet.class, "/automation/admin/settings");
        contextHandler.addServlet(AutomationSwitchServerListServlet.class, "/automation/admin/switchserver");
        contextHandler.addServlet(AutomationSwitchServerFormServlet.class, "/automation/admin/switchserverform");
        contextHandler.addServlet(AutomationSwitchServerDeleteServlet.class, "/automation/admin/switchserverdelete");
        contextHandler.addServlet(AutomationDeviceListServlet.class, "/automation/admin/device");
        contextHandler.addServlet(AutomationDeviceFormTpLinkServlet.class, "/automation/admin/deviceformtplink");
        contextHandler.addServlet(AutomationDeviceFormAvmServlet.class, "/automation/admin/deviceformavm");
        contextHandler.addServlet(AutomationDeviceFormOutputServlet.class, "/automation/admin/deviceformoutput");
        contextHandler.addServlet(AutomationDeviceFormWolServlet.class, "/automation/admin/deviceformwol");
        contextHandler.addServlet(AutomationDeviceFormScriptSingleServlet.class, "/automation/admin/deviceformscriptsingle");
        contextHandler.addServlet(AutomationDeviceFormScriptDoubleServlet.class, "/automation/admin/deviceformscriptdouble");
        contextHandler.addServlet(AutomationDeviceDeleteServlet.class, "/automation/admin/devicedelete");
        contextHandler.addServlet(AutomationSensorValuesListServlet.class, "/automation/admin/sensorvalues");
        contextHandler.addServlet(AutomationSensorValuesPushServlet.class, "/automation/admin/sensorvaluespush");
        contextHandler.addServlet(AutomationSensorValuesDefaultFormServlet.class, "/automation/admin/sensorvaluesdefaultform");
        contextHandler.addServlet(AutomationSensorValuesBiStateFormServlet.class, "/automation/admin/sensorvaluesbistateform");
        contextHandler.addServlet(AutomationSensorValuesDistanceFormServlet.class, "/automation/admin/sensorvaluesdistanceform");
        contextHandler.addServlet(AutomationSensorValuesLiveBitFormServlet.class, "/automation/admin/sensorvalueslivebitform");
        contextHandler.addServlet(AutomationSensorValuesTemperatureFormServlet.class, "/automation/admin/sensorvaluestemperatureform");
        contextHandler.addServlet(AutomationSensorValuesUserAtHomeFormServlet.class, "/automation/admin/sensorvaluesuserathomeform");
        contextHandler.addServlet(AutomationSensorValuesVirtualSensorFormServlet.class, "/automation/admin/sensorvaluevirtualsensorform");
        contextHandler.addServlet(AutomationSensorValuesDeleteServlet.class, "/automation/admin/sensorvaluesdelete");
        contextHandler.addServlet(AutomationTimerListServlet.class, "/automation/admin/timer");
        contextHandler.addServlet(AutomationTimerFormServlet.class, "/automation/admin/timerform");
        contextHandler.addServlet(AutomationTimerDeleteServlet.class, "/automation/admin/timerdelete");
        contextHandler.addServlet(AutomationDashboardListServlet.class, "/automation/admin/dashboard");
        contextHandler.addServlet(AutomationRoomListServlet.class, "/automation/admin/room");
        contextHandler.addServlet(AutomationRoomFormServlet.class, "/automation/admin/roomform");
        contextHandler.addServlet(AutomationRoomOrderServlet.class, "/automation/admin/roomorder");
        contextHandler.addServlet(AutomationRoomElementsServlet.class, "/automation/admin/roomelements");
        contextHandler.addServlet(AutomationRoomButtonElementFormServlet.class, "/automation/admin/roombuttonelementform");
        contextHandler.addServlet(AutomationRoomDividerElementFormServlet.class, "/automation/admin/roomdividerelementform");
        contextHandler.addServlet(AutomationRoomSensorElementFormServlet.class, "/automation/admin/roomsensorelementform");
        contextHandler.addServlet(AutomationRoomVirtualSensorElementFormServlet.class, "/automation/admin/roomvirtualsensorelementform");
        contextHandler.addServlet(AutomationRoomElementOrderServlet.class, "/automation/admin/roomelementorder");
        contextHandler.addServlet(AutomationRoomElementDeleteServlet.class, "/automation/admin/roomelementdelete");
        contextHandler.addServlet(AutomationRoomDeleteServlet.class, "/automation/admin/roomdelete");
        contextHandler.addServlet(AutomationIconChooserServlet.class, "/automation/admin/iconchooser");
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
     * gibt den Raum-Editor zurück
     *
     * @return Raum-Editor
     */
    public RoomEditor getRoomEditor() {
        return roomEditor;
    }

    /**
     * gibt den Schalttimer-Editor zurück
     *
     * @return Schalttimer-Editor
     */
    public SwitchTimerEditor getSwitchTimerEditor() {
        return switchTimerEditor;
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

        //Scheduler Threadpool
        timerExecutor = Application.getInstance().getTimerExecutor();

        //Executor starten
        executorService = new ExecutorService();
        executorService.startService();

        //MQTT Service
        mqttService.startService();

        //AVM Update Task starten
        if(avmEditor.isActive()) {

            timerExecutor.scheduleAtFixedRate(new AvmDataUpdateService(), 15, 15, TimeUnit.SECONDS);
        }

        //TP Link Update Task starten
        timerExecutor.scheduleAtFixedRate(new TpLinkUpdateService(), 15, 15, TimeUnit.SECONDS);

        //Schalt Timer starten
        timerExecutor.scheduleAtFixedRate(new SwitchTimerService(), 15, 15, TimeUnit.SECONDS);

        //LiveBit Sensorwerte überachen
        timerExecutor.scheduleAtFixedRate(new LiveBitUpdateService(), 15, 5, TimeUnit.SECONDS);

        //Benutzer zu Hause überwachen
        timerExecutor.scheduleAtFixedRate(new UserAtHomeUpdateService(), 15, 5, TimeUnit.SECONDS);

        //Benutzer zu Hause überwachen
        timerExecutor.scheduleAtFixedRate(new VirtualSensorUpdateService(), 15, 5, TimeUnit.SECONDS);
    }

    /**
     * Speichert alle Anwendungsdaten
     */
    public void dump() {

        sensorEditor.dump();
        switchableEditor.dump();
        switchServerEditor.dump();
        switchTimerEditor.dump();
        roomEditor.dump();
    }

    /**
     * Beendet die Anwendung
     */
    public void stop() {

        try {

            executorService.stopService();
            mqttService.stopService();

            timerExecutor.shutdown();
            timerExecutor.awaitTermination(1, TimeUnit.SECONDS);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }
}
