package net.kleditzsch.SmartHome.view.automation.admin.timer;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.DoubleSwitchable;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.SingleSwitchable;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchTimerEditor;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
import net.kleditzsch.SmartHome.model.automation.global.SwitchCommand;
import net.kleditzsch.SmartHome.model.automation.switchtimer.SwitchTimer;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
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
import java.util.stream.Collectors;

public class AutomationTimerFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/automation/admin/timer/timerform.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten vorbereiten
        SwitchTimerEditor switchTimerEditor = Application.getInstance().getAutomation().getSwitchTimerEditor();
        boolean addElement = true;
        SwitchTimer switchTimer = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Schaltserver laden
            ReentrantReadWriteLock.ReadLock lock = switchTimerEditor.readLock();
            try {

                ID id = ID.of(req.getParameter("id").trim());
                lock.lock();

                Optional<SwitchTimer> switchTimerOptional = switchTimerEditor.getById(id);
                if(switchTimerOptional.isPresent()) {

                    switchTimer = switchTimerOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Der Timer wurde nicht gefunden");
            } finally {

                lock.unlock();
            }
        } else {

            switchTimer = new SwitchTimer();
            switchTimer.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("timer", switchTimer);

        //Schaltbare Elemente auflisten
        SwitchableEditor switchableEditor = Application.getInstance().getAutomation().getSwitchableEditor();
        ReentrantReadWriteLock.ReadLock switchableLock = switchableEditor.readLock();
        switchableLock.lock();

        Map<String, Switchable> doubleSwitchables = switchableEditor.getData().stream()
                .filter(e -> e instanceof DoubleSwitchable)
                .sorted((e1, e2) -> e1.getName().compareToIgnoreCase(e2.getName()))
                .collect(Collectors.toMap(e -> e.getId().get(), e -> e));
        model.with("doubleSwitchables", doubleSwitchables);
        Map<String, Switchable> singleSwitchables = switchableEditor.getData().stream()
                .filter(e -> e instanceof SingleSwitchable)
                .sorted((e1, e2) -> e1.getName().compareToIgnoreCase(e2.getName()))
                .collect(Collectors.toMap(e -> e.getId().get(), e -> e));
        model.with("singleSwitchables", singleSwitchables);
        List<String> usedElementIds = switchTimer.getCommands().stream().map(e -> e.getSwitchableId().get()).collect(Collectors.toList());
        model.with("usedElementIds", usedElementIds);
        model.with("usedElementCount", usedElementIds.size());

        switchableLock.unlock();

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String idStr = req.getParameter("id");
        String addElementStr = req.getParameter("addElement");
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String[] month = req.getParameterValues("month");
        String[] weekday = req.getParameterValues("weekday");
        String[] day = req.getParameterValues("day");
        String[] hour = req.getParameterValues("hour");
        String[] minute = req.getParameterValues("minute");
        Map<String, String> commands = new HashMap<>();
        for(int i = 0; i < 100; i++) {

            if(req.getParameter("doubleSwitchableElement_" + i) != null && req.getParameter("doubleSwitchableCommand_" + i) != null) {

                commands.put(req.getParameter("doubleSwitchableElement_" + i), req.getParameter("doubleSwitchableCommand_" + i));
            }
            if(req.getParameter("singleSwitchableElement_" + i) != null) {

                commands.put(req.getParameter("singleSwitchableElement_" + i), "on");
            }
        }
        String disabledStr = req.getParameter("disabled");

        //Daten vorbereiten
        boolean addElement = true, disabled = false;
        ID id = null;
        List<Integer> monthList = new ArrayList<>(10);
        List<Integer> weekdayList = new ArrayList<>(10);
        List<Integer> dayList = new ArrayList<>(15);
        List<Integer> hourList = new ArrayList<>(10);
        List<Integer> minuteList = new ArrayList<>(30);
        List<SwitchCommand> switchCommands = new ArrayList<>(100);

        //Daten prüfen
        boolean success = true;
        try {

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
            //Zeiten
            if(!(month.length >= 1 && month[0] != null && month[0].equals("*"))) {

                for(int i = 0; i < month.length; i++) {

                    monthList.add(Integer.parseInt(month[i]));
                }
            }
            if(!(weekday.length >= 1 && weekday[0] != null && weekday[0].equals("*"))) {

                for(int i = 0; i < weekday.length; i++) {

                    weekdayList.add(Integer.parseInt(weekday[i]));
                }
            }
            if(!(day.length >= 1 && day[0] != null && day[0].equals("*"))) {

                for (int i = 0; i < day.length; i++) {

                    dayList.add(Integer.parseInt(day[i]));
                }
            }
            if(!(hour.length >= 1 && hour[0] != null && hour[0].equals("*"))) {

                for (int i = 0; i < hour.length; i++) {

                    hourList.add(Integer.parseInt(hour[i]));
                }
            }
            if(!(minute.length >= 1 && minute[0] != null && minute[0].equals("*"))) {

                for (int i = 0; i < minute.length; i++) {

                    minuteList.add(Integer.parseInt(minute[i]));
                }
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
            //Deaktiviert und Invertiert
            disabled = disabledStr != null && disabledStr.equalsIgnoreCase("on");

        } catch (Exception e) {

            success = false;
        }

        if (success) {

            SwitchTimerEditor switchTimerEditor = Application.getInstance().getAutomation().getSwitchTimerEditor();
            ReentrantReadWriteLock.WriteLock lock = switchTimerEditor.writeLock();
            lock.lock();

            if(addElement) {

                //neues Element hinzufügen
                SwitchTimer switchTimer = new SwitchTimer();
                switchTimer.setId(ID.create());
                switchTimer.setName(name);
                switchTimer.setDescription(description);
                switchTimer.getMonth().clear();
                switchTimer.getMonth().addAll(monthList);
                switchTimer.getWeekday().clear();
                switchTimer.getWeekday().addAll(weekdayList);
                switchTimer.getDay().clear();
                switchTimer.getDay().addAll(dayList);
                switchTimer.getHour().clear();
                switchTimer.getHour().addAll(hourList);
                switchTimer.getMinute().clear();
                switchTimer.getMinute().addAll(minuteList);
                switchTimer.getCommands().clear();
                switchTimer.getCommands().addAll(switchCommands);
                switchTimer.setDisabled(disabled);
                switchTimer.setNextExecutionTime(switchTimerEditor.calculateNextExecutionTime(switchTimer));
                switchTimerEditor.getData().add(switchTimer);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Der Timer wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/automation/admin/timer");
            } else {

                //Element bearbeiten
                Optional<SwitchTimer> switchTimerOptional = switchTimerEditor.getById(id);
                if(switchTimerOptional.isPresent() && switchTimerOptional.get() instanceof SwitchTimer) {

                    SwitchTimer switchTimer = switchTimerOptional.get();
                    switchTimer.setName(name);
                    switchTimer.setDescription(description);
                    switchTimer.getMonth().clear();
                    switchTimer.getMonth().addAll(monthList);
                    switchTimer.getWeekday().clear();
                    switchTimer.getWeekday().addAll(weekdayList);
                    switchTimer.getDay().clear();
                    switchTimer.getDay().addAll(dayList);
                    switchTimer.getHour().clear();
                    switchTimer.getHour().addAll(hourList);
                    switchTimer.getMinute().clear();
                    switchTimer.getMinute().addAll(minuteList);
                    switchTimer.getCommands().clear();
                    switchTimer.getCommands().addAll(switchCommands);
                    switchTimer.setDisabled(disabled);
                    switchTimer.setNextExecutionTime(switchTimerEditor.calculateNextExecutionTime(switchTimer));

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Timer wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/automation/admin/timer");
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der Timer konnte nicht gefunden werden");
                    resp.sendRedirect("/automation/admin/timer");
                }
            }

            lock.unlock();

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/automation/admin/timer");
        }
    }
}
