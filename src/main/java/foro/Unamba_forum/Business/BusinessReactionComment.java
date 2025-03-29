package foro.Unamba_forum.Business;

import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoReactionComment;
import foro.Unamba_forum.Entity.TCommentPublication;
import foro.Unamba_forum.Entity.TReactionComment;
import foro.Unamba_forum.Entity.TResponseComment;
import foro.Unamba_forum.Entity.TUser;
import foro.Unamba_forum.Repository.RepoCommentPublication;
import foro.Unamba_forum.Repository.RepoReactionComment;
import foro.Unamba_forum.Repository.RepoResponseComment;
import foro.Unamba_forum.Repository.RepoUser;

@Service
public class BusinessReactionComment {

    @Autowired
    private RepoReactionComment repoReaction;

    @Autowired
    private RepoCommentPublication repoComment;

    @Autowired
    private RepoResponseComment repoResponse;
    @Autowired
    private RepoUser repoUser;

    // Agregar una reacción
    public void addReaction(DtoReactionComment dtoReaction) {
        TReactionComment reaction = new TReactionComment();
        reaction.setIdReaccion(UUID.randomUUID().toString());
        reaction.setFechaReaccion(new Timestamp(System.currentTimeMillis()));

        if (dtoReaction.getIdComentario() != null) {
            TCommentPublication comment = repoComment.findById(dtoReaction.getIdComentario())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Comment not found with id: " + dtoReaction.getIdComentario()));
            reaction.setComentario(comment);
        }

        if (dtoReaction.getIdRespuesta() != null) {
            TResponseComment response = repoResponse.findById(dtoReaction.getIdRespuesta())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Response not found with id: " + dtoReaction.getIdRespuesta()));
            reaction.setRespuesta(response);
        }
        
        if (reaction.getComentario() == null && reaction.getRespuesta() == null) {
            throw new IllegalArgumentException("Either idComentario or idRespuesta must be provided");
        }

        TUser user = repoUser.findById(dtoReaction.getIdUsuario())
                .orElseThrow(
                        () -> new IllegalArgumentException("User not found with id: " + dtoReaction.getIdUsuario()));
        reaction.setUsuario(user);

        reaction.setTipo(dtoReaction.getTipo());

        repoReaction.save(reaction);
        dtoReaction.setIdReaccion(reaction.getIdReaccion());
        dtoReaction.setFechaReaccion(reaction.getFechaReaccion());

    }

    // Verificar si un usuario ya ha reaccionado a un comentario
    public boolean hasUserReactedToComment(String idUsuario, String idComentario) {
        return repoReaction.existsByUsuarioIdUsuarioAndComentarioIdComentario(idUsuario, idComentario);
    }
    // Contar reacciones de un comentario
    public long countReactionsByComment(String idComentario) {
        return repoReaction.countByComentarioIdComentario(idComentario);
    }

    // Contar reacciones de una respuesta
    public long countReactionsByResponse(String idRespuesta) {
        return repoReaction.countByRespuestaIdRespuesta(idRespuesta);
    }

    // Remover una reacción
    public void removeReaction(String idReaction) {
        TReactionComment reaction = repoReaction.findById(idReaction)
                .orElseThrow(() -> new IllegalArgumentException("Reaction not found with id: " + idReaction));
        repoReaction.delete(reaction);
    }
}
