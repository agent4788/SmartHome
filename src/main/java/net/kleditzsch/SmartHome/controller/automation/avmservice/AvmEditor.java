package net.kleditzsch.SmartHome.controller.automation.avmservice;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.database.DatabaseEditor;
import net.kleditzsch.SmartHome.model.global.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.message.Message;
import net.kleditzsch.SmartHome.model.global.settings.BooleanSetting;
import net.kleditzsch.SmartHome.model.global.settings.StringSetting;
import net.kleditzsch.SmartHome.util.api.avm.Device.SmarthomeDevice;
import net.kleditzsch.SmartHome.util.api.avm.Exception.AuthException;
import net.kleditzsch.SmartHome.util.api.avm.FritzBoxSmarthome;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AvmEditor implements DatabaseEditor {

    /**
     * Lock objekt
     */
    private volatile ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    /**
     * gibt an ob der Support aktiviert ist
     */
    boolean active = false;

    /**
     * FritzBox Objekt
     */
    private volatile FritzBoxSmarthome fritzBoxSmarthome;

    /**
     * Geräteliste
     */
    private List<SmarthomeDevice> deviceList = new ArrayList<>();

    /**
     * Zugangsdaten
     */
    private String address = "";
    private String user = "";
    private String password = "";

    /**
     * initialisiert die Verbindung
     */
    public AvmEditor() {

        //Einstellungen laden
        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock lock = settingsEditor.getReadWriteLock().readLock();
        lock.lock();

        Optional<BooleanSetting> activeOptional = settingsEditor.getBooleanSetting(SettingsEditor.AUTOMATION_FB_ACTIVE);
        active = activeOptional.isPresent() ? activeOptional.get().getValue() : false;
        Optional<StringSetting> addressOptional = settingsEditor.getStringSetting(SettingsEditor.AUTOMATION_FB_ADDRESS);
        address = activeOptional.isPresent() ? addressOptional.get().getValue() : "";
        Optional<StringSetting> userOptional = settingsEditor.getStringSetting(SettingsEditor.AUTOMATION_FB_USER);
        user = activeOptional.isPresent() ? userOptional.get().getValue() : "";
        Optional<StringSetting> passwordOptional = settingsEditor.getStringSetting(SettingsEditor.AUTOMATION_FB_PASSWORD);
        password = activeOptional.isPresent() ? passwordOptional.get().getValue() : "";

        lock.unlock();

        if(!active) {
            return;
        }

        //Verbindungsobjekt initalisieren
        try {

            fritzBoxSmarthome = new FritzBoxSmarthome(address, user, password);
        } catch (AuthException e) {

            //nochmal probieren
            try {

                fritzBoxSmarthome = new FritzBoxSmarthome(address, user, password);
            } catch (AuthException e1) {

                LoggerUtil.getLogger(this.getClass()).warning("Die Verbindung zur Fritz Box konnte nicht hergestellt werden, Meldung: \"" + e1.getLocalizedMessage() + "\"");
                MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Die Verbindung zur Fritz Box konnte nicht hergestellt werden, Meldung: \"" + e1.getLocalizedMessage() + "\"", e));
            }
        }
    }

    /**
     * Fragt die Geräteliste
     */
    @Override
    public void load() {

        if(active) {

            try {

                List<SmarthomeDevice> devices = null;
                try {

                    devices = fritzBoxSmarthome.listDevices();
                } catch (AuthException e) {

                    //nochmal probieren
                    try {

                        devices = fritzBoxSmarthome.listDevices();
                    } catch (Exception e1) {

                        try {

                            //erneuten Login probieren
                            fritzBoxSmarthome = new FritzBoxSmarthome(address, user, password);
                            devices = fritzBoxSmarthome.listDevices();
                        } catch (Exception e2) {

                            LoggerUtil.getLogger(this.getClass()).warning("Die Verbindung zur Fritz Box konnte nicht hergestellt werden, Meldung: \"" + e1.getLocalizedMessage() + "\"");
                            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Die Verbindung zur Fritz Box konnte nicht hergestellt werden, Meldung: \"" + e1.getLocalizedMessage() + "\"", e));
                        }
                    }
                }

                if(devices != null) {

                    ReentrantReadWriteLock.WriteLock lock = readWriteLock.writeLock();
                    lock.lock();

                    deviceList.clear();
                    deviceList.addAll(devices);

                    lock.unlock();
                }

            } catch (IOException e) {

                LoggerUtil.getLogger(this.getClass()).warning("Die Verbindung zur FritzBox konnte nicht hergestellt werden");
                MessageEditor.addMessage(new Message("automation", Message.Type.warning, "Die Verbindung zur Fritz Box konnte nicht hergestellt werden, Meldung: \"" + e.getLocalizedMessage() + "\"", e));
            }
        } else {

            LoggerUtil.getLogger(this.getClass()).info("AVM Support nicht aktiviert");
            MessageEditor.addMessage(new Message("automation", Message.Type.warning, "AVM Support nicht aktiviert"));
        }
    }

    /**
     * gibt die Liste mit allen SmartHome Geräten zurück
     *
     * @return Liste mit allen SmartHome Geräten
     */
    public List<SmarthomeDevice> getDeviceList() {

        return deviceList;
    }

    /**
     * gibt wenn vorhanden das Gerät mit der übergebenen Identifizierung zurück
     *
     * @param identifier Identifizierung
     * @return SMartHome Gerät
     */
    public Optional<SmarthomeDevice> getDeviceByIdentifier(String identifier) {

        return deviceList.stream()
                .filter(dev -> {
                    String i1 = identifier.trim().replaceAll("\\s", "").toLowerCase();
                    String i2 = dev.getIdentifier().trim().replaceAll("\\s", "").toLowerCase();
                    return i1.equals(i2);
                }).findFirst();
    }

    /**
     * gibt an ob der Support aktiviert ist
     *
     * @return
     */
    public boolean isActive() {
        return active;
    }

    @Override
    public void dump() {}

    /**
     * gibt das Lockobjekt zurück
     *
     * @return Lockobjekt
     */
    @Override
    public ReentrantReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    /**
     * gibt ein Lock Objekt zum erlangen des Leselock zurück
     *
     * @return Lock Objekt
     */
    @Override
    public ReentrantReadWriteLock.ReadLock readLock() {
        return readWriteLock.readLock();
    }

    /**
     * gibt ein Lock Objekt zum erlangen des Schreiblock zurück
     *
     * @return Lock Objekt
     */
    @Override
    public ReentrantReadWriteLock.WriteLock writeLock() {
        return readWriteLock.writeLock();
    }
}
