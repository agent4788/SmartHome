package net.kleditzsch.applications.automation;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.application.Application;
import net.kleditzsch.applications.automation.controller.avmservice.AvmDataUpdateService;
import net.kleditzsch.applications.automation.controller.avmservice.AvmEditor;
import net.kleditzsch.applications.automation.controller.executorservice.ExecutorService;
import net.kleditzsch.applications.automation.controller.mqttservice.MqttService;
import net.kleditzsch.applications.automation.controller.sensorservice.LiveBitUpdateService;
import net.kleditzsch.applications.automation.controller.sensorservice.UserAtHomeUpdateService;
import net.kleditzsch.applications.automation.controller.sensorservice.VirtualSensorUpdateService;
import net.kleditzsch.applications.automation.controller.switchtimerservice.SwitchTimerService;
import net.kleditzsch.applications.automation.controller.tplinkservice.TpLinkUpdateService;
import net.kleditzsch.applications.automation.model.editor.ActorEditor;
import net.kleditzsch.applications.automation.model.editor.RoomEditor;
import net.kleditzsch.applications.automation.model.editor.SensorEditor;
import net.kleditzsch.applications.automation.model.editor.SwitchTimerEditor;
import net.kleditzsch.applications.automation.view.admin.AutomationAdminIndexServlet;
import net.kleditzsch.applications.automation.view.admin.AutomationIconChooserServlet;
import net.kleditzsch.applications.automation.view.admin.actor.*;
import net.kleditzsch.applications.automation.view.admin.room.*;
import net.kleditzsch.applications.automation.view.admin.sensorvalues.*;
import net.kleditzsch.applications.automation.view.admin.settings.AutomationSettingsServlet;
import net.kleditzsch.applications.automation.view.admin.timer.AutomationTimerDeleteServlet;
import net.kleditzsch.applications.automation.view.admin.timer.AutomationTimerFormServlet;
import net.kleditzsch.applications.automation.view.admin.timer.AutomationTimerListServlet;
import net.kleditzsch.applications.automation.view.user.*;
import net.kleditzsch.smarthome.application.MetaData;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Hauptklasse der Automatisierungsanwendung
 */
public class AutomationAppliaction implements Application {

    /**
     * Meta Informationen
     */
    private static MetaData meta = new MetaData(
            "Automation",
            "/automation/",
            "/automation/index",
            "automation.png"
    );

    /**
     * Sensor Editor
     */
    private volatile SensorEditor sensorEditor;

    /**
     * Aktor Editor
     */
    private volatile ActorEditor actorEditor;

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
     * gibt den Eindeutigen Namen der Anwendung zurück
     *
     * @return Eindeutiger Name der Anwendung
     */
    @Override
    public String getApplicationName() {
        return "automation";
    }

    /**
     * gibt die Meta Informationen der Anwendung zurück
     *
     * @return Meta Informationen
     */
    @Override
    public MetaData getMetaData() {
        return meta;
    }

    /**
     * Initlisiert die Anwendungsdaten
     */
    public void init() {

        sensorEditor = new SensorEditor();
        sensorEditor.load();

        actorEditor = new ActorEditor();
        actorEditor.load();

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

        contextHandler.addServlet(AutomationIndexServlet.class, "/automation/index");
        contextHandler.addServlet(AutomationRoomServlet.class, "/automation/room");
        contextHandler.addServlet(AutomationSwitchServlet.class, "/automation/switch");
        contextHandler.addServlet(AutomationMoveShutterServlet.class, "/automation/moveshutter");
        contextHandler.addServlet(AutomationSseSyncServlet.class, "/automation/ssesync");

        contextHandler.addServlet(AutomationAdminIndexServlet.class, "/automation/admin/index");
        contextHandler.addServlet(AutomationSettingsServlet.class, "/automation/admin/settings");
        contextHandler.addServlet(AutomationActorListServlet.class, "/automation/admin/actor");
        contextHandler.addServlet(AutomationActorFormTpLinkServlet.class, "/automation/admin/actorformtplink");
        contextHandler.addServlet(AutomationActorFormAvmServlet.class, "/automation/admin/actorformavm");
        contextHandler.addServlet(AutomationActorFormMqttSingleServlet.class, "/automation/admin/actorformmqttsingle");
        contextHandler.addServlet(AutomationActorFormMqttDoubleServlet.class, "/automation/admin/actorformmqttdouble");
        contextHandler.addServlet(AutomationActorFormMqttShutterServlet.class, "/automation/admin/actorformmqttshutter");
        contextHandler.addServlet(AutomationActorFormWolServlet.class, "/automation/admin/actorformwol");
        contextHandler.addServlet(AutomationActorFormScriptSingleServlet.class, "/automation/admin/actorformscriptsingle");
        contextHandler.addServlet(AutomationActorFormScriptDoubleServlet.class, "/automation/admin/actorformscriptdouble");
        contextHandler.addServlet(AutomationActorDeleteServlet.class, "/automation/admin/actordelete");
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
        contextHandler.addServlet(AutomationRoomShutterElementFormServlet.class, "/automation/admin/roomshutterelementform");
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
     * gibt den Aktor Editor zurück
     *
     * @return Aktor Editor
     */
    public ActorEditor getActorEditor() {
        return actorEditor;
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
     * gibt den MQTT Service zurück
     *
     * @return MQTT Service
     */
    public MqttService getMqttService() {
        return mqttService;
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
        timerExecutor = SmartHome.getInstance().getTimerExecutor();

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
        actorEditor.dump();
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
