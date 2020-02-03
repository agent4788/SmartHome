package net.kleditzsch.SmartHome.view.automation.admin.room;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Actor;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.DoubleSwitchable;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.SingleSwitchable;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.editor.RoomEditor;
import net.kleditzsch.SmartHome.model.automation.editor.ActorEditor;
import net.kleditzsch.SmartHome.model.automation.global.SwitchCommand;
import net.kleditzsch.SmartHome.model.automation.room.Interface.RoomElement;
import net.kleditzsch.SmartHome.model.automation.room.Room;
import net.kleditzsch.SmartHome.model.automation.room.element.ButtonElement;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;
import net.kleditzsch.SmartHome.util.icon.IconUtil;
import net.kleditzsch.SmartHome.util.file.FileUtil;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AutomationRoomButtonElementFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/room/roombuttonelementform.html");
        JtwigModel model = JtwigModel.newModel();

        //Load List of Icon Files
        try {

            List<String> iconCategorys = IconUtil.listIconCategorys();
            model.with("iconCategorys", iconCategorys.stream().sorted().collect(Collectors.toList()));

            //liste der Font Aweasome Icons
            List<String> fontAwaesomeIcons = IconUtil.listFonAweasomeIcons();
            model.with("fontAwaesomeIcons", fontAwaesomeIcons);

        } catch (URISyntaxException e) {

            throw new IOException("Laden der Dateinamen fehlgeschlagen", e);
        }

        //Daten vorbereiten
        RoomEditor roomEditor = Application.getInstance().getAutomation().getRoomEditor();
        ReentrantReadWriteLock.ReadLock lock = roomEditor.readLock();
        lock.lock();

        boolean addElement = true;
        Room room = null;
        ButtonElement roomElement = null;

        if(req.getParameter("roomid") != null) {

            //Raum laden
            try {

                ID roomid = ID.of(req.getParameter("roomid").trim());

                Optional<Room> roomOptional = roomEditor.getById(roomid);
                if(roomOptional.isPresent()) {

                    room = roomOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

                if(req.getParameter("id") != null) {

                    //bestehendes Element laden
                    addElement = false;

                    ID id = ID.of(req.getParameter("id").trim());
                    Optional<RoomElement> roomElementOptional = room.getRoomElements().stream().filter(element -> element.getId().equals(id)).findFirst();
                    if(roomElementOptional.isPresent() && roomElementOptional.get() instanceof ButtonElement) {

                        roomElement = (ButtonElement) roomElementOptional.get();
                    } else {

                        model.with("error", "Das Raumelement wurde nicht gefunden");
                    }
                } else {

                    // neues Element erstellen
                    roomElement = new ButtonElement();
                    roomElement.setId(ID.create());
                }

                model.with("addElement", addElement);
                model.with("roomelement", roomElement);
                model.with("room", room);

            } catch (Exception e) {

                model.with("error", "Der Raum wurde nicht gefunden");
            }
        } else {

            model.with("error", "Der Raum wurde nicht gefunden");
        }

        lock.unlock();

        //Schaltbare Elemente auflisten
        if(room != null && roomElement != null) {

            ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
            ReentrantReadWriteLock.ReadLock switchableLock = actorEditor.readLock();
            switchableLock.lock();

            Map<String, Switchable> doubleSwitchables = actorEditor.getData().stream()
                    .filter(e -> e instanceof DoubleSwitchable)
                    .map(e -> (DoubleSwitchable) e)
                    .sorted((e1, e2) -> e1.getName().compareToIgnoreCase(e2.getName()))
                    .collect(Collectors.toMap(e -> e.getId().get(), e -> e));
            model.with("doubleSwitchables", doubleSwitchables);
            Map<String, Switchable> singleSwitchables = actorEditor.getData().stream()
                    .filter(e -> e instanceof SingleSwitchable)
                    .map(e -> (SingleSwitchable) e)
                    .sorted((e1, e2) -> e1.getName().compareToIgnoreCase(e2.getName()))
                    .collect(Collectors.toMap(e -> e.getId().get(), e -> e));
            model.with("singleSwitchables", singleSwitchables);
            List<String> usedElementIds = roomElement.getCommands().stream().map(e -> e.getSwitchableId().get()).collect(Collectors.toList());
            model.with("usedElementIds", usedElementIds);
            model.with("usedElementCount", usedElementIds.size());

            switchableLock.unlock();
        }

        model.with("dashboard", req.getParameter("dash") != null);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean dashboard = req.getParameter("dash") != null;

        String roomStr = req.getParameter("roomid");
        String idStr = req.getParameter("id");
        String addElementStr = req.getParameter("addElement");
        String name = req.getParameter("name");
        String displayText = req.getParameter("displayText");
        String description = req.getParameter("description");
        String iconFile = req.getParameter("iconFile");
        String onButtonText = req.getParameter("onbuttontext");
        String offButtonText = req.getParameter("offbuttontext");
        Map<String, String> commands = new HashMap<>();
        for(int i = 0; i < 100; i++) {

            if(req.getParameter("doubleSwitchableElement_" + i) != null && req.getParameter("doubleSwitchableCommand_" + i) != null) {

                commands.put(req.getParameter("doubleSwitchableElement_" + i), req.getParameter("doubleSwitchableCommand_" + i));
            }
            if(req.getParameter("singleSwitchableElement_" + i) != null) {

                commands.put(req.getParameter("singleSwitchableElement_" + i), "on");
            }
        }
        String safetyRequestEnabledStr = req.getParameter("safetyRequestEnabled");
        String safetyRequestIcon = req.getParameter("safetyRequestIcon");
        String safetyRequestHeaderText = req.getParameter("safetyRequestHeaderText");
        String safetyRequestText = req.getParameter("safetyRequestText");
        String safetyRequestExecuteButtonText = req.getParameter("safetyRequestExecuteButtonText");
        String safetyRequestCancelButtonText = req.getParameter("safetyRequestCancelButtonText");
        String disabledStr = req.getParameter("disabled");

        //Daten vorbereiten
        boolean addElement = true, disabled = true, safetyRequestEnabled = false, isDoubleSwitch = false;
        ID id = null, roomId = null;
        List<SwitchCommand> switchCommands = new ArrayList<>(100);

        //Daten prüfen
        boolean success = true;
        try {

            //ID
            if(!(idStr != null)) {

                success = false;
            }
            id = ID.of(idStr);
            if(!(roomStr != null)) {

                success = false;
            }
            roomId = ID.of(roomStr);
            //Element hinzufügen
            if(!(addElementStr != null && (addElementStr.equals("1") || addElementStr.equals("0")))) {

                success = false;
            } else {

                addElement = addElementStr.equals("1");
            }
            //Name
            if(!(name != null && name.length() >= 3 && name.length() <= 50)) {

                success = false;
            }
            //Anzeigetext
            if(!(displayText != null && displayText.length() >= 3 && displayText.length() <= 50)) {

                success = false;
            }
            //Icon Datei
            List<String> fileNames = FileUtil.listResourceDirectoryFileNames("/webserver/static/img/iconset");
            if(!fileNames.contains(iconFile.substring(iconFile.indexOf("/") + 1))) {

                success = false;
            }
            //Beschreibung
            if(!(description != null && description.length() <= 250)) {

                success = false;
            }
            //Button Texte
            if(!(onButtonText != null && onButtonText.length() >= 1 && onButtonText.length() <= 10)) {

                success = false;
            }
            //Button Texte
            if(!(offButtonText != null && offButtonText.length() >= 1 && offButtonText.length() <= 10)) {

                success = false;
            }
            //Befehle
            for(Map.Entry command : commands.entrySet()) {

                SwitchCommands switchCommand = SwitchCommands.off;
                if (command.getValue().equals("on")) {

                    switchCommand = SwitchCommands.on;
                } else if (command.getValue().equals("toggle")) {

                    switchCommand = SwitchCommands.toggle;
                }
                switchCommands.add(new SwitchCommand(ID.of((String) command.getKey()), switchCommand));
            }
            //Sicherheitsabfrage
            safetyRequestEnabled = safetyRequestEnabledStr != null && safetyRequestEnabledStr.equalsIgnoreCase("on");
            if(safetyRequestEnabled) {

                //Icon
                if(!(safetyRequestIcon != null && IconUtil.listFonAweasomeIcons().contains(safetyRequestIcon.trim()))) {

                    success = false;
                }
                //Header
                if(!(safetyRequestHeaderText != null && safetyRequestHeaderText.length() >= 3 && safetyRequestHeaderText.length() <= 20)) {

                    success = false;
                }
                //Text
                if(!(safetyRequestText != null && safetyRequestText.length() >= 3 && safetyRequestText.length() <= 250)) {

                    success = false;
                }
                //Ausführungs Botton
                if(!(safetyRequestExecuteButtonText != null && safetyRequestExecuteButtonText.length() >= 1 && safetyRequestExecuteButtonText.length() <= 10)) {

                    success = false;
                }
                //Abbrechen Botton
                if(!(safetyRequestCancelButtonText != null && safetyRequestCancelButtonText.length() >= 1 && safetyRequestCancelButtonText.length() <= 10)) {

                    success = false;
                }
            }
            //Deaktiviert
            disabled = disabledStr != null && disabledStr.equalsIgnoreCase("on");

        } catch (Exception e) {

            success = false;
        }

        //Button Art ermitteln
        ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
        ReentrantReadWriteLock.ReadLock switchableLock = actorEditor.readLock();
        switchableLock.lock();

        for (SwitchCommand command : switchCommands) {

            Optional<Actor> actorOptional = actorEditor.getById(command.getSwitchableId());
            if(actorOptional.isPresent() && actorOptional.get() instanceof DoubleSwitchable) {

                isDoubleSwitch = true;
                break;
            }
        }

        switchableLock.unlock();

        if (success) {

            RoomEditor roomEditor = Application.getInstance().getAutomation().getRoomEditor();
            ReentrantReadWriteLock.WriteLock lock = roomEditor.writeLock();
            lock.lock();

            Optional<Room> roomOptional = roomEditor.getById(roomId);
            if(roomOptional.isPresent()) {

                if(addElement) {

                    //neues Element hinzufügen
                    ButtonElement roomElement = new ButtonElement();
                    roomElement.setId(ID.create());
                    roomElement.setName(name);
                    roomElement.setDisplayText(displayText);
                    roomElement.setDescription(description);
                    roomElement.setIconFile(iconFile);
                    roomElement.setOnButtonText(onButtonText);
                    roomElement.setOffButtonText(offButtonText);
                    roomElement.getCommands().clear();
                    roomElement.getCommands().addAll(switchCommands);
                    roomElement.setSafetyRequestEnabled(safetyRequestEnabled);
                    if(safetyRequestEnabled) {

                        roomElement.setSafetyRequestIcon(safetyRequestIcon);
                        roomElement.setSafetyRequestHeaderText(safetyRequestHeaderText);
                        roomElement.setSafetyRequestText(safetyRequestText);
                        roomElement.setSafetyRequestExecuteButtonText(safetyRequestExecuteButtonText);
                        roomElement.setSafetyRequestCancelButtonText(safetyRequestCancelButtonText);
                    }
                    int nextOrderId = 0;
                    if (roomOptional.get().getRoomElements().size() > 0) {

                        nextOrderId = roomOptional.get().getRoomElements().stream().mapToInt(RoomElement::getOrderId).summaryStatistics().getMax() + 1;
                        nextOrderId = nextOrderId >= 0 ? nextOrderId : 0;
                    }
                    roomElement.setOrderId(nextOrderId);
                    roomElement.setDisabled(disabled);
                    roomElement.setDoubleButton(isDoubleSwitch);
                    roomOptional.get().getRoomElements().add(roomElement);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Button wurde erfolgreich hinzugefügt");
                    resp.sendRedirect("/automation/admin/roomelements?" + (dashboard ? "dash&" : "") + "roomid=" + roomId.get());
                } else {

                    //Element bearbeiten
                    final ID finalId = id;
                    Optional<RoomElement> roomElementOptional = roomOptional.get().getRoomElements().stream()
                            .filter(re -> re.getId().equals(finalId))
                            .findFirst();
                    if (roomElementOptional.isPresent() && roomElementOptional.get() instanceof ButtonElement) {

                        ButtonElement roomElement = (ButtonElement) roomElementOptional.get();
                        roomElement.setId(ID.create());
                        roomElement.setName(name);
                        roomElement.setDisplayText(displayText);
                        roomElement.setDescription(description);
                        roomElement.setIconFile(iconFile);
                        roomElement.setOnButtonText(onButtonText);
                        roomElement.setOffButtonText(offButtonText);
                        roomElement.getCommands().clear();
                        roomElement.getCommands().addAll(switchCommands);
                        roomElement.setSafetyRequestEnabled(safetyRequestEnabled);
                        if(safetyRequestEnabled) {

                            roomElement.setSafetyRequestIcon(safetyRequestIcon);
                            roomElement.setSafetyRequestHeaderText(safetyRequestHeaderText);
                            roomElement.setSafetyRequestText(safetyRequestText);
                            roomElement.setSafetyRequestExecuteButtonText(safetyRequestExecuteButtonText);
                            roomElement.setSafetyRequestCancelButtonText(safetyRequestCancelButtonText);
                        }
                        roomElement.setDoubleButton(isDoubleSwitch);
                        roomElement.setDisabled(disabled);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Der Button wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/automation/admin/roomelements?" + (dashboard ? "dash&" : "") + "roomid=" + roomId.get());
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Der Button konnte nicht gefunden werden");
                        resp.sendRedirect("/automation/admin/roomelements?" + (dashboard ? "dash&" : "") + "roomid=" + roomId.get());
                    }
                }
            } else {

                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Der Raum wurde nicht gefunden");
                resp.sendRedirect("/automation/admin/" + (dashboard ? "dashboard" : "room"));
            }

            lock.unlock();

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/automation/admin/roomelements?" + (dashboard ? "?dash&" : "") + "roomid=" + roomStr);
        }
    }
}
