package foro.Unamba_forum.Business;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoNotification;
import foro.Unamba_forum.Entity.TNotification;
import foro.Unamba_forum.Entity.TUser;
import foro.Unamba_forum.Repository.RepoNotification;
import foro.Unamba_forum.Repository.RepoUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class BusinessNotification {

    @Autowired
    private RepoNotification repoNotification;

    @Autowired
    private RepoUser repoUser;

    public Page<DtoNotification> getNotifications(String idUsuario, int page, int size) {
        TUser usuario = repoUser.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pageable pageable = PageRequest.of(page, size);
        Page<TNotification> notificationsPage = repoNotification.findByUsuarioOrderByFechaRegistroDesc(usuario, pageable);

        return notificationsPage.map(this::convertToDto);
    }

    public void createNotification(String idUsuario, String idActor, String mensaje,
            TNotification.TipoNotificacion tipo, String idRecurso) {
        TUser usuario = repoUser.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        TUser actor = repoUser.findById(idActor)
                .orElseThrow(() -> new RuntimeException("Actor no encontrado"));

        TNotification notification = new TNotification();
        notification.setIdNotificacion(UUID.randomUUID().toString());
        notification.setUsuario(usuario);
        notification.setActor(actor);
        notification.setMensaje(mensaje);
        notification.setTipo(tipo);
        notification.setIdRecurso(idRecurso);
        notification.setLeido(false);
        notification.setFechaRegistro(new Timestamp(System.currentTimeMillis()));

        repoNotification.save(notification);
    }

    public List<DtoNotification> getUnreadNotifications(String idUsuario) {
        TUser usuario = repoUser.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<TNotification> notifications = repoNotification.findByUsuarioAndLeidoFalse(usuario);
        return notifications.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public long countUnreadNotifications(String idUsuario) {
        TUser usuario = repoUser.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return repoNotification.countByUsuarioAndLeidoFalse(usuario);
    }

    public void markAsRead(String idNotificacion) {
        TNotification notification = repoNotification.findById(idNotificacion)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        notification.setLeido(true);
        repoNotification.save(notification);
    }

    private DtoNotification convertToDto(TNotification notification) {
        DtoNotification dto = new DtoNotification();
        dto.setIdNotificacion(notification.getIdNotificacion());
        dto.setMensaje(notification.getMensaje());
        dto.setTipo(notification.getTipo().name());
        dto.setIdRecurso(notification.getIdRecurso());
        dto.setLeido(notification.isLeido());
        dto.setIdActor(notification.getActor().getIdUsuario());

        dto.setFechaRegistro(notification.getFechaRegistro());

        TUser actor = notification.getActor();
        if (actor != null) {
            dto.setNombreActor(
                    actor.getPerfil() != null ? (actor.getPerfil().getNombre() + actor.getPerfil().getApellidos())
                            : "Usuario desconocido");
            dto.setAvatar(actor.getPerfil() != null ? actor.getPerfil().getFotoPerfil() : "URL predeterminada");
        }
        return dto;
    }

}
