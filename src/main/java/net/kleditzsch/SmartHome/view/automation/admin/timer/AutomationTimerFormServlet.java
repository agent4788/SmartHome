package net.kleditzsch.SmartHome.view.automation.admin.timer;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.DoubleSwitchable;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Shutter;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.SingleSwitchable;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchTimerEditor;
import net.kleditzsch.SmartHome.model.automation.editor.ActorEditor;
import net.kleditzsch.SmartHome.model.automation.global.Interface.Command;
import net.kleditzsch.SmartHome.model.automation.global.MoveCommand;
import net.kleditzsch.SmartHome.model.automation.global.SwitchCommand;
import net.kleditzsch.SmartHome.model.automation.switchtimer.SwitchTimer;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;
import net.kleditzsch.SmartHome.util.form.FormValidation;
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
import java.util.regex.Pattern;
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

        //Schaltbare Elemente und Rollläden auflisten
        ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
        ReentrantReadWriteLock.ReadLock switchableLock = actorEditor.readLock();
        switchableLock.lock();

        Map<String, Switchable> doubleSwitchables = actorEditor.getData().stream()
                .filter(e -> e instanceof DoubleSwitchable)
                .map(e -> (DoubleSwitchable) e)
                .collect(Collectors.toMap(e -> e.getId().get(), e -> e));
        model.with("doubleSwitchables", doubleSwitchables);
        Map<String, Switchable> singleSwitchables = actorEditor.getData().stream()
                .filter(e -> e instanceof SingleSwitchable)
                .map(e -> (SingleSwitchable) e)
                .collect(Collectors.toMap(e -> e.getId().get(), e -> e));
        model.with("singleSwitchables", singleSwitchables);
        List<String> usedElementIds = switchTimer.getCommands().stream()
                .filter(e -> e instanceof SwitchCommand)
                .map(e -> (SwitchCommand) e)
                .map(e -> e.getSwitchableId().get())
                .collect(Collectors.toList());
        List<String> usedShutterIds = switchTimer.getCommands().stream()
                .filter(e -> e instanceof MoveCommand)
                .map(e -> (MoveCommand) e)
                .map(e -> e.getShutterId().get())
                .collect(Collectors.toList());
        Map<String, Shutter> shutters = actorEditor.getData().stream()
                .filter(e -> e instanceof Shutter)
                .map(e -> (Shutter) e)
                .collect(Collectors.toMap(e -> e.getId().get(), e -> e));
        model.with("usedElementIds", usedElementIds);
        model.with("shutters", shutters);
        model.with("usedShutterIds", usedShutterIds);
        model.with("usedElementCount", usedElementIds.size() + usedShutterIds.size());

        switchableLock.unlock();

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        FormValidation form = FormValidation.create(req);

        //Daten vorbereiten
        List<Integer> monthList = new ArrayList<>(10);
        List<Integer> weekdayList = new ArrayList<>(10);
        List<Integer> dayList = new ArrayList<>(15);
        List<Integer> hourList = new ArrayList<>(10);
        List<Integer> minuteList = new ArrayList<>(30);

        //Formulardaten validieren
        ID id = form.getId("id", "ID");
        boolean addElement = form.optBoolean("addElement", "neues Element", false);
        String name = form.getString("name", "Name", 3, 50);
        String description = form.optString("description", "Beschreibung", "", 3, 250);
        boolean disabled = form.optBoolean("disabled", "Deaktiviert", false);

        List<String> month = form.getStringList("month", "Monat", Pattern.compile("^[\\\\*0-9]{1,2}$"));
        monthList = month.stream()
                .filter(e -> !e.equalsIgnoreCase("*"))
                .map(Integer::parseInt)
                .filter(e -> e >= 1 && e <= 12)
                .collect(Collectors.toList());
        List<String> weekday = form.getStringList("weekday", "Wochentag", Pattern.compile("^[\\\\*0-9]{1}$"));
        weekdayList = weekday.stream()
                .filter(e -> !e.equalsIgnoreCase("*"))
                .map(Integer::parseInt)
                .filter(e -> e >= 1 && e <= 7)
                .collect(Collectors.toList());
        List<String> day = form.getStringList("day", "Tag", Pattern.compile("^[\\\\*0-9]{1,2}$"));
        dayList = day.stream()
                .filter(e -> !e.equalsIgnoreCase("*"))
                .map(Integer::parseInt)
                .filter(e -> e >= 1 && e <= 31)
                .collect(Collectors.toList());
        List<String> hour = form.getStringList("hour", "Stunde", Pattern.compile("^[\\\\*0-9]{1,2}$"));
        hourList = hour.stream()
                .filter(e -> !e.equalsIgnoreCase("*"))
                .map(Integer::parseInt)
                .filter(e -> e >= 0 && e <= 23)
                .collect(Collectors.toList());
        List<String> minute = form.getStringList("minute", "Minute", Pattern.compile("^[\\\\*0-9]{1,2}$"));
        minuteList = minute.stream()
                .filter(e -> !e.equalsIgnoreCase("*"))
                .map(Integer::parseInt)
                .filter(e -> e >= 0 && e <= 59)
                .collect(Collectors.toList());

        //Schaltbare Elemente
        Map<String, String> switchCommands = new HashMap<>();
        Map<String, String> moveCommands = new HashMap<>();
        for(int i = 0; i < 100; i++) {

            //Double Switchable
            if(form.fieldExists("doubleSwitchableElement_" + i) && form.fieldExists("doubleSwitchableCommand_" + i)) {

                switchCommands.put(req.getParameter("doubleSwitchableElement_" + i), req.getParameter("doubleSwitchableCommand_" + i));
            }
            //Single Switchable
            if(form.fieldExists("singleSwitchableElement_" + i)) {

                switchCommands.put(req.getParameter("singleSwitchableElement_" + i), "on");
            }
            //Shutter
            if(form.fieldExists("shutterElement_" + i + "_id") && form.fieldExists("shutterElement_" + i + "_level")) {

                moveCommands.put(req.getParameter("shutterElement_" + i + "_id"), req.getParameter("shutterElement_" + i + "_level"));
            }
        }

        List<Command> commands = new ArrayList<>(100);
        try {

            //schaltbefehle
            for(Map.Entry<String, String> command : switchCommands.entrySet()) {

                SwitchCommands switchCommand = SwitchCommands.off;
                if (command.getValue().equals("on")) {

                    switchCommand = SwitchCommands.on;
                } else if (command.getValue().equals("toggle")) {

                    switchCommand = SwitchCommands.toggle;
                }
                commands.add(new SwitchCommand(ID.of(command.getKey()), switchCommand));
            }

            for(Map.Entry<String, String> command : moveCommands.entrySet()) {

                int level = Integer.parseInt(command.getValue());
                if(level >= 0 && level <= 100) {

                    commands.add(new MoveCommand(ID.of(command.getKey()), level));
                } else {

                    throw new Exception();
                }
            }

        } catch (Exception e) {

            form.setInvalid("element", "Ungültige Schaltbefehle");
        }

        if (form.isSuccessful()) {

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
                switchTimer.getCommands().addAll(commands);
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
                    switchTimer.getCommands().addAll(commands);
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
