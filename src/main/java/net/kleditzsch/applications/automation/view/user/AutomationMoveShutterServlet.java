package net.kleditzsch.applications.automation.view.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.model.editor.MessageEditor;
import net.kleditzsch.smarthome.model.message.Message;
import net.kleditzsch.smarthome.utility.form.FormValidation;
import net.kleditzsch.applications.automation.controller.executorservice.ExecutorService;
import net.kleditzsch.applications.automation.controller.executorservice.command.MoveShutterCommand;
import net.kleditzsch.applications.automation.model.device.actor.Interface.Actor;
import net.kleditzsch.applications.automation.model.device.actor.Interface.Shutter;
import net.kleditzsch.applications.automation.model.editor.ActorEditor;
import net.kleditzsch.applications.automation.model.editor.RoomEditor;
import net.kleditzsch.applications.automation.model.room.Interface.RoomElement;
import net.kleditzsch.applications.automation.model.room.Room;
import net.kleditzsch.applications.automation.model.room.element.ShutterElement;

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
            RoomEditor roomEditor = SmartHome.getInstance().getAutomation().getRoomEditor();
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
                ExecutorService executorService = SmartHome.getInstance().getAutomation().getExecutorService();

                //Rollläden laden und zum ausführen Übergeben
                ActorEditor actorEditor = SmartHome.getInstance().getAutomation().getActorEditor();
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
