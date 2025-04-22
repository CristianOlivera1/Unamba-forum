package foro.Unamba_forum.Business;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoCommentPublication;
import foro.Unamba_forum.Dto.DtoReactionSummaryComment;
import foro.Unamba_forum.Dto.DtoUserComment;
import foro.Unamba_forum.Dto.DtoUserProfile;
import foro.Unamba_forum.Entity.TCommentPublication;
import foro.Unamba_forum.Entity.TReactionComment;
import foro.Unamba_forum.Entity.TUserProfile;
import foro.Unamba_forum.Repository.RepoCommentPublication;
import foro.Unamba_forum.Repository.RepoPublication;
import foro.Unamba_forum.Repository.RepoReactionComment;
import foro.Unamba_forum.Repository.RepoResponseComment;
import foro.Unamba_forum.Repository.RepoUser;
import foro.Unamba_forum.Repository.RepoUserProfile;
import foro.Unamba_forum.Service.CommentPublication.RequestsObject.RequestUpdateCP;
import foro.Unamba_forum.Entity.TNotification;

@Service
public class BusinessCommentPublication {

    @Autowired
    private RepoCommentPublication repoComment;

    @Autowired
    private RepoPublication repoPublication;

    @Autowired
    private RepoUser repoUser;

    @Autowired
    private RepoUserProfile repoUserProfile;

    @Autowired
    private RepoResponseComment repoResponseComment;

    @Autowired
    private RepoReactionComment repoReactionComment;

    @Autowired
    private BusinessNotification notificacionService;

    // Agregar un comentario a una publicación
    public void addComment(DtoCommentPublication dtoComment) {
        TCommentPublication comment = new TCommentPublication();
        comment.setIdComentario(UUID.randomUUID().toString());
        comment.setPublicacion(repoPublication.findById(dtoComment.getIdPublicacion())
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada")));
        comment.setUsuario(repoUser.findById(dtoComment.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")));
        comment.setContenido(dtoComment.getContenido());
        comment.setFechaRegistro(new Timestamp(System.currentTimeMillis()));
        comment.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        repoComment.save(comment);
        dtoComment.setIdComentario(comment.getIdComentario());
        dtoComment.setFechaRegistro(comment.getFechaRegistro());
        dtoComment.setFechaActualizacion(comment.getFechaActualizacion());

        // Crear notificación para el autor de la publicación
        String idUsuarioPublicacion = comment.getPublicacion().getUsuario().getIdUsuario();
        if (!idUsuarioPublicacion.equals(dtoComment.getIdUsuario())) {
            String mensaje = "ha comentado 💬 en tu publicación sobre, " + comment.getPublicacion().getTitulo() + ".";
            notificacionService.createNotification(
                    idUsuarioPublicacion,
                    dtoComment.getIdUsuario(),
                    mensaje,
                    TNotification.TipoNotificacion.COMENTARIO,
                    comment.getIdComentario());
        }

    }

    // Obtener el total de comentarios de una publicación
    public long getTotalComments(String idPublicacion) {
        return repoComment.countByPublicacionIdPublicacion(idPublicacion);
    }

    /* Usuarios que comentaron en la publicacion */
    public List<DtoUserComment> getUsersWhoCommented(String idPublicacion) {
        // Obtener los comentarios de la publicación
        List<TCommentPublication> comments = repoComment.findByPublicacionIdPublicacion(idPublicacion);

        // Mapear los comentarios a DtoUserComment
        return comments.stream().map(comment -> {
            TUserProfile userProfile = repoUserProfile.findByUsuario(comment.getUsuario().getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Perfil de usuario no encontrado"));

            DtoUserComment dto = new DtoUserComment();
            dto.setIdUsuario(comment.getUsuario().getIdUsuario());
            dto.setNombreCompleto(userProfile.getNombre() + " " + userProfile.getApellidos());
            dto.setNombreCarrera(
                    userProfile.getIdCarrera() != null ? userProfile.getIdCarrera().getNombre() : "Sin carrera");

            dto.setAvatar(userProfile.getFotoPerfil());
            return dto;
        }).distinct().collect(Collectors.toList());
    }

    // Obtener los comentarios de una publicación
    public List<DtoCommentPublication> getCommentsByPublication(String idPublicacion) {
        List<TCommentPublication> comments = repoComment.findByPublicacionIdPublicacion(idPublicacion);

        return comments.stream().map(comment -> {
            DtoCommentPublication dto = convertToDto(comment);

            // Obtener el perfil del usuario
            TUserProfile userProfileEntity = repoUserProfile.findByUsuario(comment.getUsuario().getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Perfil de usuario no encontrado"));

            // Agregar solo el nombre completo y el avatar
            dto.setNombreCompleto(userProfileEntity.getNombre() + " " + userProfileEntity.getApellidos());
            dto.setAvatar(userProfileEntity.getFotoPerfil());

            // Obtener el número de respuestas asociadas al comentario
            long numeroRespuestas = repoResponseComment.countByComentarioIdComentario(comment.getIdComentario());
            dto.setNumeroRespuestas(numeroRespuestas);

            // Obtener el resumen de reacciones
            List<DtoReactionSummaryComment> reacciones = repoReactionComment
                    .findByComentarioIdComentario(comment.getIdComentario())
                    .stream()
                    .map(reaction -> {
                        DtoReactionSummaryComment reactionSummary = new DtoReactionSummaryComment();
                        reactionSummary.setIdReaccion(reaction.getIdReaccion()); // Incluye el ID de la reacción
                        reactionSummary.setTipo(reaction.getTipo());
                        reactionSummary.setCantidad(1); // Cada reacción individual cuenta como 1
                        return reactionSummary;
                    })
                    .collect(Collectors.toList());
            dto.setReacciones(reacciones);
            return dto;
        }).collect(Collectors.toList());
    }

    
    // Actualizar un comentario
    public DtoCommentPublication updateComment(RequestUpdateCP request) {
        TCommentPublication comment = repoComment.findById(request.getIdComentario())
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        comment.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        comment.setContenido(request.getContenido());
        repoComment.save(comment);

        return convertToDto(comment);
    }

    // Eliminar un comentario
    public void deleteComment(String idComentario) {
        TCommentPublication comment = repoComment.findById(idComentario)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        repoComment.delete(comment);
    }

    // Convertir entidad a DTO
    private DtoCommentPublication convertToDto(TCommentPublication comment) {
        DtoCommentPublication dto = new DtoCommentPublication();
        dto.setIdComentario(comment.getIdComentario());
        dto.setIdUsuario(comment.getUsuario().getIdUsuario());
        dto.setIdPublicacion(comment.getPublicacion().getIdPublicacion());
        dto.setContenido(comment.getContenido());
        dto.setFechaRegistro(comment.getFechaRegistro());
        dto.setFechaActualizacion(comment.getFechaActualizacion());
        return dto;
    }

}
