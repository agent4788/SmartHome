package net.kleditzsch.SmartHome.view.automation.admin.device;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.model.automation.device.switchable.AvmSocket;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.device.switchable.TPlinkSocket;
import net.kleditzsch.SmartHome.model.automation.editor.SensorEditor;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchServerEditor;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
import net.kleditzsch.SmartHome.model.automation.switchserver.SwitchServer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationDeviceDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);
                SwitchableEditor swe = Application.getInstance().getAutomation().getSwitchableEditor();
                ReentrantReadWriteLock.WriteLock lock = swe.writeLock();
                lock.lock();

                Optional<Switchable> switchServerOptional = swe.getData().stream().filter(sw -> sw.getId().equals(id)).findFirst();
                if(switchServerOptional.isPresent()) {

                    swe.getData().remove(switchServerOptional.get());

                    //Sensoren mit löschen
                    if(switchServerOptional.get() instanceof AvmSocket) {

                        AvmSocket avmSocket = (AvmSocket) switchServerOptional.get();
                        SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
                        ReentrantReadWriteLock.WriteLock sensorLock = sensorEditor.writeLock();
                        sensorLock.lock();

                        Optional<SensorValue> optional1 = sensorEditor.getById(avmSocket.getTempSensorId());
                        if(optional1.isPresent()) {

                            sensorEditor.getData().remove(optional1.get());
                        }
                        Optional<SensorValue> optional2 = sensorEditor.getById(avmSocket.getPowerSensorId());
                        if(optional2.isPresent()) {

                            sensorEditor.getData().remove(optional2.get());
                        }
                        Optional<SensorValue> optional3 = sensorEditor.getById(avmSocket.getEnergySensorId());
                        if(optional3.isPresent()) {

                            sensorEditor.getData().remove(optional3.get());
                        }

                        sensorLock.unlock();
                    } else if (switchServerOptional.get() instanceof TPlinkSocket) {

                        TPlinkSocket tplinkSocket = (TPlinkSocket) switchServerOptional.get();
                        if (tplinkSocket.getSocketType() == TPlinkSocket.SOCKET_TYPE.HS110) {

                            SensorEditor sensorEditor = Application.getInstance().getAutomation().getSensorEditor();
                            ReentrantReadWriteLock.WriteLock sensorLock = sensorEditor.writeLock();
                            sensorLock.lock();

                            Optional<ID> idOptional1 = tplinkSocket.getVoltageSensor();
                            if(idOptional1.isPresent()) {

                                Optional<SensorValue> optional1 = sensorEditor.getById(idOptional1.get());
                                if(optional1.isPresent()) {

                                    sensorEditor.getData().remove(optional1.get());
                                }
                            }
                            Optional<ID> idOptional2 = tplinkSocket.getCurrentSensor();
                            if(idOptional2.isPresent()) {

                                Optional<SensorValue> optional2 = sensorEditor.getById(idOptional2.get());
                                if(optional2.isPresent()) {

                                    sensorEditor.getData().remove(optional2.get());
                                }
                            }
                            Optional<ID> idOptional3 = tplinkSocket.getPowerSensorId();
                            if(idOptional3.isPresent()) {

                                Optional<SensorValue> optional3 = sensorEditor.getById(idOptional3.get());
                                if(optional3.isPresent()) {

                                    sensorEditor.getData().remove(optional3.get());
                                }
                            }
                            Optional<ID> idOptional4 = tplinkSocket.getEnergySensorId();
                            if(idOptional4.isPresent()) {

                                Optional<SensorValue> optional4 = sensorEditor.getById(idOptional4.get());
                                if(optional4.isPresent()) {

                                    sensorEditor.getData().remove(optional4.get());
                                }
                            }

                            sensorLock.unlock();
                        }
                    }
                } else {

                    success = false;
                }

                lock.unlock();

            } catch (Exception e) {

                success = false;
            }
        }

        if (success) {

            //löschem i.O.
            req.getSession().setAttribute("success", true);
            req.getSession().setAttribute("message", "Das Gerät wurde erfolgreich gelöscht");
            resp.sendRedirect("/automation/admin/device");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Das Gerät konnte nicht gelöscht werden");
            resp.sendRedirect("/automation/admin/device");
        }
    }
}
