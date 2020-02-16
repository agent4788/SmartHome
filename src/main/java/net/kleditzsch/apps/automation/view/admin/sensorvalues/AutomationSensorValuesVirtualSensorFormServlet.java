package net.kleditzsch.apps.automation.view.admin.sensorvalues;

import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.automation.model.device.sensor.*;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.SensorValue;
import net.kleditzsch.apps.automation.model.device.sensor.Interface.VirtualSensorValue;
import net.kleditzsch.apps.automation.model.device.sensor.virtual.*;
import net.kleditzsch.apps.automation.model.editor.SensorEditor;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationSensorValuesVirtualSensorFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/sensorvalues/sensorvaluesvirtualsensorform.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten laden
        SensorEditor sensorEditor = SmartHome.getInstance().getAutomation().getSensorEditor();
        ReentrantReadWriteLock.ReadLock lock = sensorEditor.readLock();
        lock.lock();

        boolean addElement = true;
        int type = -1;
        VirtualSensorValue sensorValue = null;
        List<SensorValue> sensorValues = new ArrayList<>();

        //Bearbeiten
        if(req.getParameter("id") != null) {

            addElement = false;

            //Virtuellen Sensor laden
            try {

                ID id = ID.of(req.getParameter("id").trim());

                Optional<SensorValue> virtualSensorValueOptional = sensorEditor.getById(id);
                if(virtualSensorValueOptional.isPresent()) {

                    if(virtualSensorValueOptional.get() instanceof VirtualSensorValue) {

                        sensorValue = (VirtualSensorValue) virtualSensorValueOptional.get();
                    } else {

                        model.with("error", "Der Virtuelle Sensor konnte nicht gefunden werden");
                    }
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Der Virtuelle Sensor konnte nicht gefunden werden");
            }
        } else if(req.getParameter("type") != null) {

            //erstellen
            try {

                type = Integer.parseInt(req.getParameter("type"));
            } catch (NumberFormatException e) {}

            if(type > 0) {

                switch (type) {

                    case 1:

                        sensorValue = new VirtualActualPowerValue(ID.create(), "abc", "abc");
                        break;
                    case 2:

                        sensorValue = new VirtualEnergyValue(ID.create(), "abc", "abc");
                        break;
                    case 3:

                        sensorValue = new VirtualGasAmountValue(ID.create(), "abc", "abc");
                        break;
                    case 4:

                        sensorValue = new VirtualLightIntensityValue(ID.create(), "abc", "abc");
                        break;
                    case 5:

                        sensorValue = new VirtualTemperatureValue(ID.create(), "abc", "abc");
                        break;
                    case 6:

                        sensorValue = new VirtualWaterAmountValue(ID.create(), "abc", "abc");
                        break;
                    default:

                        model.with("error", "Ungültiger Typ");
                        break;
                }
            } else {

                model.with("error", "Ungültiger Typ");
            }
        } else {

            model.with("error", "Ungültiger Typ");
        }

        //Sensoren Laden
        switch (sensorValue.getType()) {

            case VIRTUALSENSORVALUE_ACTUAL_POWER:

                sensorValues = sensorEditor.filterByType(ActualPowerValue.class);
                type = 1;
                break;
            case VIRTUALSENSORVALUE_ENERGY:

                sensorValues = sensorEditor.filterByType(EnergyValue.class);
                type = 2;
                break;
            case VIRTUALSENSORVALUE_GAS_AMOUNT:

                sensorValues = sensorEditor.filterByType(GasAmountValue.class);
                type = 3;
                break;
            case VIRTUALSENSORVALUE_LIGHT_INTENSITY:

                sensorValues = sensorEditor.filterByType(LightIntensityValue.class);
                type = 4;
                break;
            case VIRTUALSENSORVALUE_TEMPERATURE:

                sensorValues = sensorEditor.filterByType(TemperatureValue.class);
                type = 5;
                break;
            case VIRTUALSENSORVALUE_WATER_AMOUNT:

                sensorValues = sensorEditor.filterByType(WaterAmountValue.class);
                type = 6;
                break;
            default:

                model.with("error", "Ungültiger Typ");
                break;
        }
        model.with("type", type);
        model.with("addElement", addElement);
        model.with("sensorValue", sensorValue);
        model.with("sensorValues", sensorValues);

        lock.unlock();

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String idStr = req.getParameter("id");
        String addElementStr = req.getParameter("addElement");
        String typeStr = req.getParameter("type");
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String[] sensorVeluesArray = req.getParameterValues("sensorValues");
        List<String> sensorValues = Arrays.asList(sensorVeluesArray);

        //Daten vorbereiten
        boolean addElement = true;
        int type = -1;
        ID id = null;

        //Daten prüfen
        boolean success = true;
        try {

            //Typ
            if(!(typeStr != null)) {

                success = false;
            }
            type = Integer.parseInt(typeStr);
            //ID
            if(!(idStr != null)) {

                success = false;
            }
            id = ID.of(idStr);
            //neues Element
            if(!(addElementStr != null && (addElementStr.equals("1") || addElementStr.equals("0")))) {

                success = false;
            } else {

                addElement = addElementStr.equals("1");
            }
            //Name
            if(!(name != null && name.length() >= 3 && name.length() <= 50)) {

                success = false;
            }
            //Beschreibung
            if(!(description != null && description.length() <= 250)) {

                success = false;
            }
            //Sensorwerte
            sensorValues.forEach(ID::of);

        } catch (Exception e) {

            success = false;
        }

        if (success) {

            SensorEditor sensorEditor = SmartHome.getInstance().getAutomation().getSensorEditor();
            ReentrantReadWriteLock.WriteLock lock = sensorEditor.writeLock();
            lock.lock();

            Optional<SensorValue> sensorValueOptional;

            switch (type) {

                case 1:

                    if(addElement) {

                        //hinzufügen
                        VirtualActualPowerValue sensorValue = new VirtualActualPowerValue(ID.create(), ID.create().get(), name);
                        sensorValue.setDescription(description);
                        for (String idString : sensorValues) {

                            sensorValueOptional = sensorEditor.getById(idString);
                            if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof ActualPowerValue) {

                                sensorValue.getSensorValues().add(idString);
                            }
                        }
                        sensorEditor.getData().add(sensorValue);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Der Virtuelle Sensor wurde erfolgreich erstellt");
                        resp.sendRedirect("/automation/admin/sensorvalues");
                    } else {

                        //bearbeiten
                        Optional<SensorValue> virtualSensorValueOptional = sensorEditor.getById(id);
                        if(virtualSensorValueOptional.isPresent() && virtualSensorValueOptional.get() instanceof VirtualActualPowerValue) {

                            VirtualActualPowerValue sensorValue = (VirtualActualPowerValue) virtualSensorValueOptional.get();
                            sensorValue.setName(name);
                            sensorValue.setDescription(description);
                            sensorValue.getSensorValues().clear();
                            for (String idString : sensorValues) {

                                sensorValueOptional = sensorEditor.getById(idString);
                                if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof ActualPowerValue) {

                                    sensorValue.getSensorValues().add(idString);
                                }
                            }

                            req.getSession().setAttribute("success", true);
                            req.getSession().setAttribute("message", "Der Virtuelle Sensor wurde erfolgreich bearbeitet");
                            resp.sendRedirect("/automation/admin/sensorvalues");
                        } else {

                            req.getSession().setAttribute("success", false);
                            req.getSession().setAttribute("message", "Der Virtuelle Sensor konnte nicht gefunden werden");
                            resp.sendRedirect("/automation/admin/sensorvalues");
                        }
                    }
                    break;
                case 2:

                    if(addElement) {

                        //hinzufügen
                        VirtualEnergyValue sensorValue = new VirtualEnergyValue(ID.create(), ID.create().get(), name);
                        sensorValue.setDescription(description);
                        for (String idString : sensorValues) {

                            sensorValueOptional = sensorEditor.getById(idString);
                            if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof EnergyValue) {

                                sensorValue.getSensorValues().add(idString);
                            }
                        }
                        sensorEditor.getData().add(sensorValue);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Der Virtuelle Sensor wurde erfolgreich erstellt");
                        resp.sendRedirect("/automation/admin/sensorvalues");
                    } else {

                        //bearbeiten
                        Optional<SensorValue> virtualSensorValueOptional = sensorEditor.getById(id);
                        if(virtualSensorValueOptional.isPresent() && virtualSensorValueOptional.get() instanceof VirtualEnergyValue) {

                            VirtualEnergyValue sensorValue = (VirtualEnergyValue) virtualSensorValueOptional.get();
                            sensorValue.setName(name);
                            sensorValue.setDescription(description);
                            sensorValue.getSensorValues().clear();
                            for (String idString : sensorValues) {

                                sensorValueOptional = sensorEditor.getById(idString);
                                if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof EnergyValue) {

                                    sensorValue.getSensorValues().add(idString);
                                }
                            }

                            req.getSession().setAttribute("success", true);
                            req.getSession().setAttribute("message", "Der Virtuelle Sensor wurde erfolgreich bearbeitet");
                            resp.sendRedirect("/automation/admin/sensorvalues");
                        } else {

                            req.getSession().setAttribute("success", false);
                            req.getSession().setAttribute("message", "Der Virtuelle Sensor konnte nicht gefunden werden");
                            resp.sendRedirect("/automation/admin/sensorvalues");
                        }
                    }
                    break;
                case 3:

                    if(addElement) {

                        //hinzufügen
                        VirtualGasAmountValue sensorValue = new VirtualGasAmountValue(ID.create(), ID.create().get(), name);
                        sensorValue.setDescription(description);
                        for (String idString : sensorValues) {

                            sensorValueOptional = sensorEditor.getById(idString);
                            if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof GasAmountValue) {

                                sensorValue.getSensorValues().add(idString);
                            }
                        }
                        sensorEditor.getData().add(sensorValue);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Der Virtuelle Sensor wurde erfolgreich erstellt");
                        resp.sendRedirect("/automation/admin/sensorvalues");
                    } else {

                        //bearbeiten
                        Optional<SensorValue> virtualSensorValueOptional = sensorEditor.getById(id);
                        if(virtualSensorValueOptional.isPresent() && virtualSensorValueOptional.get() instanceof VirtualGasAmountValue) {

                            VirtualGasAmountValue sensorValue = (VirtualGasAmountValue) virtualSensorValueOptional.get();
                            sensorValue.setName(name);
                            sensorValue.setDescription(description);
                            sensorValue.getSensorValues().clear();
                            for (String idString : sensorValues) {

                                sensorValueOptional = sensorEditor.getById(idString);
                                if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof GasAmountValue) {

                                    sensorValue.getSensorValues().add(idString);
                                }
                            }

                            req.getSession().setAttribute("success", true);
                            req.getSession().setAttribute("message", "Der Virtuelle Sensor wurde erfolgreich bearbeitet");
                            resp.sendRedirect("/automation/admin/sensorvalues");
                        } else {

                            req.getSession().setAttribute("success", false);
                            req.getSession().setAttribute("message", "Der Virtuelle Sensor konnte nicht gefunden werden");
                            resp.sendRedirect("/automation/admin/sensorvalues");
                        }
                    }
                    break;
                case 4:

                    if(addElement) {

                        //hinzufügen
                        VirtualLightIntensityValue sensorValue = new VirtualLightIntensityValue(ID.create(), ID.create().get(), name);
                        sensorValue.setDescription(description);
                        for (String idString : sensorValues) {

                            sensorValueOptional = sensorEditor.getById(idString);
                            if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof LightIntensityValue) {

                                sensorValue.getSensorValues().add(idString);
                            }
                        }
                        sensorEditor.getData().add(sensorValue);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Der Virtuelle Sensor wurde erfolgreich erstellt");
                        resp.sendRedirect("/automation/admin/sensorvalues");
                    } else {

                        //bearbeiten
                        Optional<SensorValue> virtualSensorValueOptional = sensorEditor.getById(id);
                        if(virtualSensorValueOptional.isPresent() && virtualSensorValueOptional.get() instanceof VirtualLightIntensityValue) {

                            VirtualLightIntensityValue sensorValue = (VirtualLightIntensityValue) virtualSensorValueOptional.get();
                            sensorValue.setName(name);
                            sensorValue.setDescription(description);
                            sensorValue.getSensorValues().clear();
                            for (String idString : sensorValues) {

                                sensorValueOptional = sensorEditor.getById(idString);
                                if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof LightIntensityValue) {

                                    sensorValue.getSensorValues().add(idString);
                                }
                            }

                            req.getSession().setAttribute("success", true);
                            req.getSession().setAttribute("message", "Der Virtuelle Sensor wurde erfolgreich bearbeitet");
                            resp.sendRedirect("/automation/admin/sensorvalues");
                        } else {

                            req.getSession().setAttribute("success", false);
                            req.getSession().setAttribute("message", "Der Virtuelle Sensor konnte nicht gefunden werden");
                            resp.sendRedirect("/automation/admin/sensorvalues");
                        }
                    }
                    break;
                case 5:

                    if(addElement) {

                        //hinzufügen
                        VirtualTemperatureValue sensorValue = new VirtualTemperatureValue(ID.create(), ID.create().get(), name);
                        sensorValue.setDescription(description);
                        for (String idString : sensorValues) {

                            sensorValueOptional = sensorEditor.getById(idString);
                            if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof TemperatureValue) {

                                sensorValue.getSensorValues().add(idString);
                            }
                        }
                        sensorEditor.getData().add(sensorValue);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Der Virtuelle Sensor wurde erfolgreich erstellt");
                        resp.sendRedirect("/automation/admin/sensorvalues");
                    } else {

                        //bearbeiten
                        Optional<SensorValue> virtualSensorValueOptional = sensorEditor.getById(id);
                        if(virtualSensorValueOptional.isPresent() && virtualSensorValueOptional.get() instanceof VirtualTemperatureValue) {

                            VirtualTemperatureValue sensorValue = (VirtualTemperatureValue) virtualSensorValueOptional.get();
                            sensorValue.setName(name);
                            sensorValue.setDescription(description);
                            sensorValue.getSensorValues().clear();
                            for (String idString : sensorValues) {

                                sensorValueOptional = sensorEditor.getById(idString);
                                if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof TemperatureValue) {

                                    sensorValue.getSensorValues().add(idString);
                                }
                            }

                            req.getSession().setAttribute("success", true);
                            req.getSession().setAttribute("message", "Der Virtuelle Sensor wurde erfolgreich bearbeitet");
                            resp.sendRedirect("/automation/admin/sensorvalues");
                        } else {

                            req.getSession().setAttribute("success", false);
                            req.getSession().setAttribute("message", "Der Virtuelle Sensor konnte nicht gefunden werden");
                            resp.sendRedirect("/automation/admin/sensorvalues");
                        }
                    }
                    break;
                case 6:

                    if(addElement) {

                        //hinzufügen
                        VirtualWaterAmountValue sensorValue = new VirtualWaterAmountValue(ID.create(), ID.create().get(), name);
                        sensorValue.setDescription(description);
                        for (String idString : sensorValues) {

                            sensorValueOptional = sensorEditor.getById(idString);
                            if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof WaterAmountValue) {

                                sensorValue.getSensorValues().add(idString);
                            }
                        }
                        sensorEditor.getData().add(sensorValue);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Der Virtuelle Sensor wurde erfolgreich erstellt");
                        resp.sendRedirect("/automation/admin/sensorvalues");
                    } else {

                        //bearbeiten
                        Optional<SensorValue> virtualSensorValueOptional = sensorEditor.getById(id);
                        if(virtualSensorValueOptional.isPresent() && virtualSensorValueOptional.get() instanceof VirtualWaterAmountValue) {

                            VirtualWaterAmountValue sensorValue = (VirtualWaterAmountValue) virtualSensorValueOptional.get();
                            sensorValue.setName(name);
                            sensorValue.setDescription(description);
                            sensorValue.getSensorValues().clear();
                            for (String idString : sensorValues) {

                                sensorValueOptional = sensorEditor.getById(idString);
                                if(sensorValueOptional.isPresent() && sensorValueOptional.get() instanceof WaterAmountValue) {

                                    sensorValue.getSensorValues().add(idString);
                                }
                            }

                            req.getSession().setAttribute("success", true);
                            req.getSession().setAttribute("message", "Der Virtuelle Sensor wurde erfolgreich bearbeitet");
                            resp.sendRedirect("/automation/admin/sensorvalues");
                        } else {

                            req.getSession().setAttribute("success", false);
                            req.getSession().setAttribute("message", "Der Virtuelle Sensor konnte nicht gefunden werden");
                            resp.sendRedirect("/automation/admin/sensorvalues");
                        }
                    }
                    break;
                default:

                    //Eingaben n.i.O.
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Ungültiger Typ");
                    resp.sendRedirect("/automation/admin/sensorvalues");
                    break;
            }

            lock.unlock();
        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/automation/admin/sensorvalues");
        }
    }
}
