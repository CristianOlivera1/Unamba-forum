package foro.Unamba_forum.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import foro.Unamba_forum.Entity.TResponseComment;

@Repository
public interface RepoResponseComment extends JpaRepository<TResponseComment, String> {
    List<TResponseComment> findByComentarioIdComentarioOrderByFechaRegistroDesc(String idComentario);
    List<TResponseComment> findByRespuestaPadreIdRespuesta(String idRespuestaPadre);

    long countByComentarioIdComentario(String idComentario);

}