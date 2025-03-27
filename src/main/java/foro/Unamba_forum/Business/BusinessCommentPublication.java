package foro.Unamba_forum.Business;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoCommentPublication;
import foro.Unamba_forum.Entity.TCommentPublication;
import foro.Unamba_forum.Repository.RepoCommentPublication;
import foro.Unamba_forum.Repository.RepoPublication;
import foro.Unamba_forum.Repository.RepoUser;
import foro.Unamba_forum.Service.CommentPublication.RequestsObject.RequestUpdateCP;

@Service
public class BusinessCommentPublication {
    
    @Autowired
    private RepoCommentPublication repoComment;

    @Autowired
    private RepoPublication repoPublication;

    @Autowired
    private RepoUser repoUser;

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

    }

    // Obtener el total de comentarios de una publicación
    public long getTotalComments(String idPublicacion) {
        return repoComment.countByPublicacionIdPublicacion(idPublicacion);
    }

    // Obtener los comentarios de una publicación
    public List<DtoCommentPublication> getCommentsByPublication(String idPublicacion) {
        List<TCommentPublication> comments = repoComment.findByPublicacionIdPublicacion(idPublicacion);
        return comments.stream().map(this::convertToDto).collect(Collectors.toList());
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
