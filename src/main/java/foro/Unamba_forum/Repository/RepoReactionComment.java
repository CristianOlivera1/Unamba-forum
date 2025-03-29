package foro.Unamba_forum.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import foro.Unamba_forum.Entity.TReactionComment;

@Repository
public interface RepoReactionComment extends JpaRepository<TReactionComment, String> {
    long countByComentarioIdComentario(String idComentario);
    long countByRespuestaIdRespuesta(String idRespuesta);

    boolean existsByUsuarioIdUsuarioAndComentarioIdComentario(String idUsuario, String idComentario);
}
