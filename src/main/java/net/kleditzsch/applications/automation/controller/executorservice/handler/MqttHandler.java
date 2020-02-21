package net.kleditzsch.applications.automation.controller.executorservice.handler;

import com.google.common.base.Preconditions;
import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.options.SwitchCommands;
import net.kleditzsch.smarthome.utility.datetime.DatabaseDateTimeUtil;
import net.kleditzsch.applications.automation.controller.mqttservice.MqttService;
import net.kleditzsch.applications.automation.model.device.AutomationElement;
import net.kleditzsch.applications.automation.model.device.actor.Interface.Actor;
import net.kleditzsch.applications.automation.model.device.actor.Interface.Switchable;
import net.kleditzsch.applications.automation.model.device.actor.switchable.MqttDouble;
import net.kleditzsch.applications.automation.model.device.actor.switchable.MqttSingle;
import net.kleditzsch.applications.automation.model.editor.ActorEditor;

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

            successfull = SmartHome.getInstance().getAutomation().getMqttService().publish(topic, now.getBytes(), 2);
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

            successfull = SmartHome.getInstance().getAutomation().getMqttService().publish(topic, now.getBytes(), 2);
        }

        //neuen Status speichern (wenn erfolgreich ausgeführt)
        final Switchable.State finalNewState = newState;
        if(mqttSingle != null && successfull) {

            //Einfaches MQTT Element
            ActorEditor actorEditor = SmartHome.getInstance().getAutomation().getActorEditor();
            ReentrantReadWriteLock.WriteLock lock = actorEditor.writeLock();
            lock.lock();

            Optional<Actor> actorOptional = actorEditor.getById(mqttSingle.getId());
            if(actorOptional.isPresent() && actorOptional.get() instanceof Switchable) {

                Switchable switchable = (Switchable) actorOptional.get();
                switchable.setLastToggleTime(LocalDateTime.now());
            }

            lock.unlock();
        } else if (mqttDouble != null && successfull) {

            //Doppeltes MQTT Element
            ActorEditor actorEditor = SmartHome.getInstance().getAutomation().getActorEditor();
            ReentrantReadWriteLock.WriteLock lock = actorEditor.writeLock();
            lock.lock();

            Optional<Actor> actorOptional = actorEditor.getById(mqttDouble.getId());
            if(actorOptional.isPresent() && actorOptional.get() instanceof Switchable) {

                Switchable switchable = (Switchable) actorOptional.get();
                switchable.setState(finalNewState);
                switchable.setLastToggleTime(LocalDateTime.now());
            }

            lock.unlock();
        }
    }
}
