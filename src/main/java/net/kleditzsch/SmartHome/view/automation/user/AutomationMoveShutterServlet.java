package net.kleditzsch.SmartHome.view.automation.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.controller.automation.executorservice.ExecutorService;
import net.kleditzsch.SmartHome.controller.automation.executorservice.command.MoveShutterCommand;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Actor;
import net.kleditzsch.SmartHome.model.automation.device.actor.Interface.Shutter;
import net.kleditzsch.SmartHome.model.automation.editor.ActorEditor;
import net.kleditzsch.SmartHome.model.automation.editor.RoomEditor;
import net.kleditzsch.SmartHome.model.automation.global.SwitchCommand;
import net.kleditzsch.SmartHome.model.automation.room.Interface.RoomElement;
import net.kleditzsch.SmartHome.model.automation.room.Room;
import net.kleditzsch.SmartHome.model.automation.room.element.ButtonElement;
import net.kleditzsch.SmartHome.model.automation.room.element.ShutterElement;
import net.kleditzsch.SmartHome.model.global.editor.MessageEditor;
import net.kleditzsch.SmartHome.model.global.message.Message;
import net.kleditzsch.SmartHome.util.form.FormValidation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationMoveShutterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;
        String message = "";

        FormValidation form = FormValidation.create(req);
        ID roomId = form.getId("roomid", "Raum ID");
        ID elementId = form.getId("id", "Element ID");
        int targetLevel = form.getInteger("level", "Ziel niveau", 0, 100);

        if(form.isSuccessful()) {

            List<ID> shutterIds = new ArrayList<>();

            //Raum element suchen und die Rollladen IDs ermitteln
            RoomEditor roomEditor = Application.getInstance().getAutomation().getRoomEditor();
            ReentrantReadWriteLock.ReadLock lock = roomEditor.readLock();
            lock.lock();

            //Raum laden
            Optional<Room> roomOptional = roomEditor.getById(roomId);
            if(roomOptional.isPresent()) {

                //Raumelement suchen
                Optional<RoomElement> roomElementOptional = roomOptional.get().getRoomElements().stream()
                        .filter(e -> e.getId().equals(elementId))
                        .findFirst();
                if(roomElementOptional.isPresent() && roomElementOptional.get() instanceof ShutterElement) {

                    ShutterElement shutterElement = (ShutterElement) roomElementOptional.get();
                    shutterIds.addAll(shutterElement.getShutterIds());
                    MessageEditor.addMessage(new Message("automation", Message.Type.info, "Rollladen \"" + shutterElement.getDisplayText() +"\" manuell auf " + targetLevel + " % gefahren"));
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

                //Rollläden laden und zum ausführen Übergeben
                ActorEditor actorEditor = Application.getInstance().getAutomation().getActorEditor();
                ReentrantReadWriteLock.ReadLock actorLock = actorEditor.readLock();
                actorLock.lock();

                for (ID shutterId : shutterIds) {

                    Optional<Actor> shutterOptional = actorEditor.getById(shutterId);
                    if(shutterOptional.isPresent() && shutterOptional.get() instanceof Shutter) {

                        executorService.putCommand(
                                new MoveShutterCommand(
                                        (Shutter) actorEditor.copyOf(shutterOptional.get()),
                                        targetLevel
                                )
                        );
                    } else {

                        success = false;
                        message = "Es konnten nicht alle Befehle ausgeführt werden";
                    }
                }

                actorLock.unlock();
            }

        } else {

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
