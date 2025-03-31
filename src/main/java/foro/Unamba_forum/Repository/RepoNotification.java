package foro.Unamba_forum.Repository;

import java.util.List;
import org.springframework.data.domain.Page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import foro.Unamba_forum.Entity.TNotification;
import foro.Unamba_forum.Entity.TUser;

public interface RepoNotification extends JpaRepository<TNotification, String>{
    List<TNotification> findByUsuarioAndLeidoFalse(TUser usuario); // Notificaciones no leídas
    long countByUsuarioAndLeidoFalse(TUser usuario); // Contador de notificaciones no leídas
    Page<TNotification> findByUsuarioOrderByFechaRegistroDesc(TUser usuario, Pageable pageable);

}