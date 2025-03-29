package foro.Unamba_forum.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import foro.Unamba_forum.Entity.TReactionComment;

@Repository
public interface RepoReactionComment extends JpaRepository<TReactionComment, String> {
    long countByComentarioIdComentario(String idComentario);
    long countByRespuestaIdRespuesta(String idRespuesta);

    boolean existsByUsuarioIdUsuarioAndComentarioIdComentario(String idUsuario, String idComentario);

    List<TReactionComment> findByComentarioIdComentario(String idComentario);


    List<TReactionComment> findByComentarioIdComentarioAndTipo(String idComentario, String tipo);
}
