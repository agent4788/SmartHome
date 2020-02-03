package net.kleditzsch.SmartHome.view.automation.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.executorservice.ExecutorService;
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
import net.kleditzsch.SmartHome.model.global.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.global.message.Message;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationSwitchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean success = true;
        String message = "";
        try {

            ID roomId = ID.of(req.getParameter("roomid"));
            ID devId = ID.of(req.getParameter("id"));
            String command = req.getParameter("command");

            if(command!= null && (command.equals("on") || command.equals("off") || command.equals("toggle"))) {

                List<SwitchCommand> commands = new ArrayList<>(50);

                RoomEditor roomEditor = Application.getInstance().getAutomation().getRoomEditor();
                ReentrantReadWriteLock.ReadLock lock = roomEditor.readLock();
                lock.lock();

                //Raum laden
                Optional<Room> roomOptional = roomEditor.getById(roomId);
                if(roomOptional.isPresent()) {

                    //Raumelement suchen
                    Optional<RoomElement> roomElementOptional = roomOptional.get().getRoomElements().stream()
                            .filter(e -> e.getId().equals(devId))
                            .findFirst();
                    if(roomElementOptional.isPresent() && roomElementOptional.get() instanceof ButtonElement) {

                        ButtonElement buttonElement = (ButtonElement) roomElementOptional.get();
                        buttonElement.getCommands().forEach(switchCommand -> {

                            commands.add(new SwitchCommand(switchCommand.getSwitchableId(), switchCommand.getCommand()));
                        });
                        MessageEditor.addMessage(new Message("automation", Message.Type.info, "Button \"" + buttonElement.getDisplayText() +"\" manuell " + (command.equals("on") ? "eingeschalten" : (command.equals("off") ? "ausgeschalten" : "umgeschalten"))));
                    } else {

                        success = false;
                        message = "Das Element wurde nicht gefunden";
                    }
                } else {

                    success = false;
                    message = "Der Raum wurde nicht gefunden";
                }

                lock.unlock();

                if(success) {

                    //Ausführungsservice
                    ExecutorService executorService = Application.getInstance().getAutomation().getExecutorService();

                    //Befehle zum ausführen übergeben
                    ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
                    ReentrantReadWriteLock.ReadLock actorLock = actorEditor.readLock();
                    actorLock.lock();

                    for (SwitchCommand switchCommand : commands) {

                        Optional<Actor> actorOptional = actorEditor.getById(switchCommand.getSwitchableId());
                        if(actorOptional.isPresent()) {

                            if(actorOptional.get() instanceof DoubleSwitchable) {

                                SwitchCommands comm = SwitchCommands.on;
                                if(command.equals("on")) {

                                    comm = switchCommand.getCommand();
                                } else if(command.equals("off")) {

                                    if(switchCommand.getCommand() == SwitchCommands.toggle) {

                                        comm = SwitchCommands.toggle;
                                    } else {

                                        comm = switchCommand.getCommand() == SwitchCommands.on ? SwitchCommands.off : SwitchCommands.on;
                                    }
                                } else if(command.equals("toggle")) {

                                    comm = SwitchCommands.toggle;
                                }

                                executorService.putCommand(
                                        new net.kleditzsch.SmartHome.controller.automation.executorservice.command.SwitchCommand(
                                                (Switchable) actorEditor.copyOf(actorOptional.get()),
                                            comm
                                        )
                                );
                            } else if(actorOptional.get() instanceof SingleSwitchable) {

                                if(command.equals("on")) {

                                    executorService.putCommand(
                                            new net.kleditzsch.SmartHome.controller.automation.executorservice.command.SwitchCommand(
                                                    (Switchable) actorEditor.copyOf(actorOptional.get()),
                                                SwitchCommands.on
                                            )
                                    );
                                }
                            }
                        } else {

                            success = false;
                            message = "Es konnten nicht alle Befehle ausgeführt werden";
                        }
                    }

                    actorLock.unlock();
                }

            } else {

                throw new Exception();
            }

        } catch (Exception e) {

            success = false;
            message = "Feherhafte Eingaben";
        }

        //JSON erzeugen
        JsonObject jo = new JsonObject();
        jo.add("success", new JsonPrimitive(success));
        jo.add("message", new JsonPrimitive(message));
        String json = jo.toString();

        //Template rendern
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print(json);
    }
}
