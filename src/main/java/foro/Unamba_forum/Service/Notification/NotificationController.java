package foro.Unamba_forum.Service.Notification;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import foro.Unamba_forum.Business.BusinessNotification;
import foro.Unamba_forum.Dto.DtoNotification;
import foro.Unamba_forum.Service.Generic.ResponseGeneric;


@RestController
@RequestMapping("/notification")
public class NotificationController {
  @Autowired
    private BusinessNotification businessNotification;


    @GetMapping("/all/{idUsuario}")
public ResponseEntity<ResponseGeneric<List<DtoNotification>>> getNotifications(
        @PathVariable String idUsuario,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {
    ResponseGeneric<List<DtoNotification>> response = new ResponseGeneric<>();
    try {
        List<DtoNotification> notifications = businessNotification.getNotifications(idUsuario, page, size).getContent();
        response.setType("success");
        response.setData(notifications);
        response.setListMessage(List.of("Notificaciones obtenidas correctamente"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        response.setType("error");
        response.setListMessage(List.of("Error al obtener las notificaciones: " + e.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    @GetMapping("/unread/{idUsuario}")
    public ResponseEntity<ResponseGeneric<List<DtoNotification>>> getUnreadNotifications(@PathVariable String idUsuario) {
        ResponseGeneric<List<DtoNotification>> response = new ResponseGeneric<>();
        try {
            List<DtoNotification> notifications = businessNotification.getUnreadNotifications(idUsuario);
            response.setType("success");
            response.setData(notifications);
            response.setListMessage(List.of("Notificaciones no leídas obtenidas correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener las notificaciones no leídas: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/count/{idUsuario}")
    public ResponseEntity<ResponseGeneric<Long>> countUnreadNotifications(@PathVariable String idUsuario) {
        ResponseGeneric<Long> response = new ResponseGeneric<>();
        try {
            long count = businessNotification.countUnreadNotifications(idUsuario);
            response.setType("success");
            response.setData(count);
            response.setListMessage(List.of("Cantidad de notificaciones no leídas obtenida correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener la cantidad de notificaciones no leídas: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/read/{idNotificacion}")
    public ResponseEntity<ResponseGeneric<Void>> markAsRead(@PathVariable String idNotificacion) {
        ResponseGeneric<Void> response = new ResponseGeneric<>();
        try {
            businessNotification.markAsRead(idNotificacion);
            response.setType("success");
            response.setListMessage(List.of("Notificación marcada como leída correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al marcar la notificación como leída: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
