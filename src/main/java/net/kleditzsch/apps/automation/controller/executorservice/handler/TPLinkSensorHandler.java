package net.kleditzsch.apps.automation.controller.executorservice.handler;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.ActualPowerValue;
import net.kleditzsch.apps.automation.model.device.sensor.CurrentValue;
import net.kleditzsch.apps.automation.model.device.sensor.EnergyValue;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;
import net.kleditzsch.apps.automation.model.device.sensor.VoltageValue;
import net.kleditzsch.apps.automation.model.device.actor.switchable.TPlinkSocket;
import net.kleditzsch.apps.automation.model.editor.SensorEditor;
import net.kleditzsch.SmartHome.model.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.message.Message;
import net.kleditzsch.apps.automation.api.tplink.HS110;
import net.kleditzsch.SmartHome.utility.logger.LoggerUtil;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TPLinkSensorHandler implements Runnable {

    /**
     * schaltbares Element
     */
    private TPlinkSocket socket;

    /**
     * @param socket schaltbares Element
     */
    public TPLinkSensorHandler(TPlinkSocket socket) {

        Preconditions.checkNotNull(socket);
        this.socket = socket;
    }

    @Override
    public void run() {

        if(socket.getSocketType() == TPlinkSocket.SOCKET_TYPE.HS110) {

            ReentrantReadWriteLock.WriteLock lock = null;
            boolean success = false;
            Exception exception = null;
            HS110 hs110 = new HS110(socket.getIpAddress(), socket.getPort());

            for(int i = 0; i < 3; i++) {

                try {

                    HS110.EnergyData energyData = hs110.getEnergyData();

                    SensorEditor sensorEditor = SmartHome.getInstance().getAutomation().getSensorEditor();
                    lock = sensorEditor.writeLock();
                    lock.lock();

                    //Spannungssensor
                    Optional<ID> voltageSensorId = socket.getVoltageSensorId();
                    if(voltageSensorId.isPresent()) {

                        Optional<SensorValue> sensorValueOptional1 = sensorEditor.getById(voltageSensorId.get());
                        if(sensorValueOptional1.isPresent() && sensorValueOptional1.get() instanceof VoltageValue) {

                            VoltageValue voltageValue = (VoltageValue) sensorValueOptional1.get();
                            voltageValue.pushVoltage(energyData.getNowVoltage() * 1000);
                        }
                    }

                    //Stromsensor
                    Optional<ID> currentSensorId = socket.getCurrentSensorId();
                    if(currentSensorId.isPresent()) {

                        Optional<SensorValue> sensorValueOptional2 = sensorEditor.getById(currentSensorId.get());
                        if(sensorValueOptional2.isPresent() && sensorValueOptional2.get() instanceof CurrentValue) {

                            CurrentValue currentValue = (CurrentValue) sensorValueOptional2.get();
                            currentValue.pushCurrent(energyData.getNowCurrent() * 1000);
                        }
                    }

                    //aktualenergie
                    Optional<ID> powerSensorId = socket.getPowerSensorId();
                    if(powerSensorId.isPresent()) {

                        Optional<SensorValue> sensorValueOptional3 = sensorEditor.getById(powerSensorId.get());
                        if(sensorValueOptional3.isPresent() && sensorValueOptional3.get() instanceof ActualPowerValue) {

                            ActualPowerValue actualPowerValue = (ActualPowerValue) sensorValueOptional3.get();
                            actualPowerValue.pushActualPower(energyData.getNowPower() * 1000);
                        }
                    }

                    //aktualenergie
                    Optional<ID> energySensorId = socket.getEnergySensorId();
                    if(energySensorId.isPresent()) {

                        Optional<SensorValue> sensorValueOptional4 = sensorEditor.getById(energySensorId.get());
                        if(sensorValueOptional4.isPresent() && sensorValueOptional4.get() instanceof EnergyValue) {

                            EnergyValue energyValue = (EnergyValue) sensorValueOptional4.get();
                            energyValue.pushEnergy(energyData.getEnergy() * 1000);
                        }
                    }

                    success = true;
                } catch (IOException e) {

                    exception = e;
                } catch (NullPointerException e) {

                    exception = e;
                } finally {

                    if(lock != null && lock.isHeldByCurrentThread()) {

                        lock.unlock();
                    }
                }

                //bei misserfolg 2,5 Sekunden warten und erneut probieren
                if (!success) {

                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException e) {

                        return;
                    }
                }
            }

            if(!success) {

                if(exception instanceof IOException) {


                    //Steckdose nicht erreichbar
                    LoggerUtil.getLogger(this.getClass()).finer("Die TP-Link Steckdose mit der IP " + socket.getIpAddress() + " konnte nicht erreicht werden");
                    MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Die TP-Link Steckdose mit der IP " + socket.getIpAddress() + " konnte nicht erreicht werden", exception));
                } else if(exception instanceof NullPointerException) {

                    //keine HS110 Steckdose
                    LoggerUtil.getLogger(this.getClass()).finer("Die TP-Link Steckdose mit der IP " + socket.getIpAddress() + " ist keine HS110 Steckdose");
                    MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Die TP-Link Steckdose mit der IP " + socket.getIpAddress() + " ist keine HS110 Steckdose"));
                }
            }
        } else {

            //Nicht als HS110 Steckdose markiert
            LoggerUtil.getLogger(this.getClass()).finer("Die TP-Link Steckdose mit der IP " + socket.getIpAddress() + " ist keine HS110 Steckdose");
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Die TP-Link Steckdose mit der IP " + socket.getIpAddress() + " ist keine HS110 Steckdose"));
        }
    }
}
