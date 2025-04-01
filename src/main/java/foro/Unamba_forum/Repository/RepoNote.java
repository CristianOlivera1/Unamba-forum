package foro.Unamba_forum.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import foro.Unamba_forum.Entity.TNote;
import foro.Unamba_forum.Entity.TUser;

public interface RepoNote extends JpaRepository<TNote, String> {
    @Query("SELECT n FROM TNote n JOIN n.carrera c WHERE c.idCarrera = :idCarrera ORDER BY n.fechaRegistro DESC")

    List<TNote> findByCarrera(@Param("idCarrera") String idCarrera);

    @Query("SELECT n FROM TNote n WHERE n.usuario = :usuario ORDER BY n.fechaRegistro DESC")
    List<TNote> findByUsuario(@Param("usuario") TUser usuario);
}