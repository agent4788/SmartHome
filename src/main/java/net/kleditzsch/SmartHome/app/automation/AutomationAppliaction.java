package net.kleditzsch.SmartHome.app.automation;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.avmservice.AvmDataUpdateService;
import net.kleditzsch.SmartHome.controller.automation.avmservice.AvmEditor;
import net.kleditzsch.SmartHome.controller.automation.executorservice.ExecutorService;
import net.kleditzsch.SmartHome.controller.automation.sensorservice.LiveBitUpdateService;
import net.kleditzsch.SmartHome.controller.automation.sensorservice.UserAtHomeUpdateService;
import net.kleditzsch.SmartHome.controller.automation.sensorservice.VirtualSensorUpdateService;
import net.kleditzsch.SmartHome.controller.automation.switchtimerservice.SwitchTimerService;
import net.kleditzsch.SmartHome.controller.automation.tplinkservice.TpLinkUpdateService;
import net.kleditzsch.SmartHome.model.automation.editor.*;
import net.kleditzsch.SmartHome.view.automation.admin.*;
import net.kleditzsch.SmartHome.view.automation.admin.device.*;
import net.kleditzsch.SmartHome.view.automation.admin.sensorvalues.*;
import net.kleditzsch.SmartHome.view.automation.admin.settings.AutomationSettingsServlet;
import net.kleditzsch.SmartHome.view.automation.admin.switchserver.AutomationSwitchServerDeleteServlet;
import net.kleditzsch.SmartHome.view.automation.admin.switchserver.AutomationSwitchServerFormServlet;
import net.kleditzsch.SmartHome.view.automation.admin.switchserver.AutomationSwitchServerListServlet;
import net.kleditzsch.SmartHome.view.automation.user.AutomationIndexServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutomationAppliaction {

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
    }

    /**
     * initalisiert die Webinhalte der Anwendung
     *
     * @param contextHandler Context Handler
     */
    public void initWebContext(ServletContextHandler contextHandler) {

        contextHandler.addServlet(AutomationIndexServlet.class, "/automation/");
        contextHandler.addServlet(AutomationIndexServlet.class, "/automation/index");
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
        ScheduledExecutorService timerExecutor = Application.getInstance().getTimerExecutor();

        //Executor starten
        executorService = new ExecutorService();
        executorService.startService();

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
        roomEditor.dump();
    }

    /**
     * Beendet die Automatisierungsanwendung
     */
    public void stop() {

        executorService.stopService();
    }
}
