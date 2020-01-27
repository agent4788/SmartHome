package net.kleditzsch.SmartHome.controller.automation.executorservice.handler;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.mqttservice.MqttService;
import net.kleditzsch.SmartHome.model.automation.device.AutomationElement;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.device.switchable.MqttDouble;
import net.kleditzsch.SmartHome.model.automation.device.switchable.MqttSingle;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;
import net.kleditzsch.SmartHome.util.datetime.DatabaseDateTimeUtil;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MqttHandler implements Runnable {

    /**
     * einfaches MQTT Gerät
     */
    private MqttSingle mqttSingle;

    /**
     * doppeltes MQTT Gerät
     */
    private MqttDouble mqttDouble;

    /**
     * Schaltbefehl
     */
    private SwitchCommands switchCommand;

    /**
     * @param mqttSingle einfaches MQTT Gerät
     */
    public MqttHandler(MqttSingle mqttSingle) {

        Preconditions.checkNotNull(mqttSingle);
        this.mqttSingle = mqttSingle;
    }

    /**
     * @param mqttDouble doppeltes MQTT Gerät
     * @param switchCommand Schaltbefehl
     */
    public MqttHandler(MqttDouble mqttDouble, SwitchCommands switchCommand) {

        Preconditions.checkNotNull(mqttDouble);
        Preconditions.checkNotNull(switchCommand);
        this.mqttDouble = mqttDouble;
        this.switchCommand = switchCommand;
    }

    @Override
    public void run() {

        Switchable.State newState = Switchable.State.OFF;
        boolean successfull = false;

        if(mqttSingle != null) {

            //Einfaches MQTT Gerät

            //deaktiviertes Gerät ignorieren
            if(mqttSingle.isDisabled()) {

                return;
            }

            //MQTT Befehl senden
            String topic = MqttService.MAIN_TOPIC + "switch/" + mqttSingle.getMqttName() + "/on";
            String now = DatabaseDateTimeUtil.getDatabaseDateTimeStr(LocalDateTime.now());

            successfull = Application.getInstance().getAutomation().getMqttService().publish(topic, now.getBytes(), 2);
        } else {

            //Doppeltes MQTT Gerät

            //deaktiviertes Gerät ignorieren
            if(mqttDouble.isDisabled()) {

                return;
            }

            String command = "off";
            switch(switchCommand) {

                case on:

                    if(mqttDouble.isInverse()) {

                        command = "off";
                        newState = AutomationElement.State.OFF;
                    } else {

                        command = "on";
                        newState = AutomationElement.State.ON;
                    }
                    break;
                case off:

                    if(mqttDouble.isInverse()) {

                        command = "on";
                        newState = AutomationElement.State.ON;
                    } else {

                        command = "off";
                        newState = AutomationElement.State.OFF;
                    }
                break;
                case toggle:

                    if(mqttDouble.getState() == AutomationElement.State.ON) {

                        if(mqttDouble.isInverse()) {

                            command = "on";
                            newState = AutomationElement.State.ON;
                        } else {

                            command = "off";
                            newState = AutomationElement.State.OFF;
                        }
                    } else {

                        if(mqttDouble.isInverse()) {

                            command = "off";
                            newState = AutomationElement.State.OFF;
                        } else {

                            command = "on";
                            newState = AutomationElement.State.ON;
                        }
                    }
                    break;
            }

            String topic = MqttService.MAIN_TOPIC + "switch/" + mqttDouble.getMqttName() + "/" + command;
            String now = DatabaseDateTimeUtil.getDatabaseDateTimeStr(LocalDateTime.now());

            successfull = Application.getInstance().getAutomation().getMqttService().publish(topic, now.getBytes(), 2);
        }

        //neuen Status speichern (wenn erfolgreich ausgeführt)
        final Switchable.State finalNewState = newState;
        if(mqttSingle != null && successfull) {

            //Einfaches Script
            SwitchableEditor switchableEditor = Application.getInstance().getAutomation().getSwitchableEditor();
            ReentrantReadWriteLock.WriteLock lock = switchableEditor.writeLock();
            lock.lock();

            Optional<Switchable> switchableOptional = switchableEditor.getById(mqttSingle.getId());
            switchableOptional.ifPresent(switchable -> {

                switchable.setLastToggleTime(LocalDateTime.now());
            });

            lock.unlock();
        } else if (mqttDouble != null && successfull) {

            //Doppeltes Script
            SwitchableEditor switchableEditor = Application.getInstance().getAutomation().getSwitchableEditor();
            ReentrantReadWriteLock.WriteLock lock = switchableEditor.writeLock();
            lock.lock();

            Optional<Switchable> switchableOptional = switchableEditor.getById(mqttDouble.getId());
            switchableOptional.ifPresent(switchable -> {

                switchable.setState(finalNewState);
                switchable.setLastToggleTime(LocalDateTime.now());
            });

            lock.unlock();
        }
    }
}
