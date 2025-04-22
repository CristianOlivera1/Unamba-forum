package foro.Unamba_forum.Business;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoReactionSummaryComment;
import foro.Unamba_forum.Dto.DtoResponseComment;
import foro.Unamba_forum.Dto.DtoUserProfile;
import foro.Unamba_forum.Entity.TCommentPublication;
import foro.Unamba_forum.Entity.TReactionComment;
import foro.Unamba_forum.Entity.TResponseComment;
import foro.Unamba_forum.Entity.TUser;
import foro.Unamba_forum.Entity.TUserProfile;
import foro.Unamba_forum.Repository.RepoCommentPublication;
import foro.Unamba_forum.Repository.RepoReactionComment;
import foro.Unamba_forum.Repository.RepoResponseComment;
import foro.Unamba_forum.Repository.RepoUser;
import foro.Unamba_forum.Repository.RepoUserProfile;

@Service
public class BusinessResponseComment {
    
    @Autowired
    private RepoResponseComment repoResponse;
    
    @Autowired
    private RepoUser repoUser;

    @Autowired
    private RepoCommentPublication repoComment;
    
    @Autowired
    private RepoUserProfile repoUserProfile;

    @Autowired
    private RepoReactionComment repoReactionComment;

    // Agregar una respuesta a un comentario o respuesta
    public void addResponse(DtoResponseComment dtoResponse) {
    if (dtoResponse.getIdComentario() == null && dtoResponse.getIdRespuestaPadre() == null) {
        throw new IllegalArgumentException("Debe especificar un comentario o una respuesta padre para la respuesta");
    }

    TResponseComment response = new TResponseComment();
    response.setIdRespuesta(UUID.randomUUID().toString());

    if (dtoResponse.getIdComentario() != null) {
        TCommentPublication comment = repoComment.findById(dtoResponse.getIdComentario())
            .orElseThrow(() -> new RuntimeException("Comentario no encontrado con ID: " + dtoResponse.getIdComentario()));
        response.setComentario(comment);
    }

    if (dtoResponse.getIdRespuestaPadre() != null) {
        TResponseComment parentResponse = repoResponse.findById(dtoResponse.getIdRespuestaPadre())
            .orElseThrow(() -> new RuntimeException("Respuesta padre no encontrada con ID: " + dtoResponse.getIdRespuestaPadre()));
        response.setRespuestaPadre(parentResponse);
    }

    TUser user = repoUser.findById(dtoResponse.getIdUsuario())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dtoResponse.getIdUsuario()));
    response.setUsuario(user);

    response.setContenido(dtoResponse.getContenido());
    response.setFechaRegistro(new Timestamp(System.currentTimeMillis()));

    repoResponse.save(response);
    dtoResponse.setIdRespuesta(response.getIdRespuesta());
    dtoResponse.setFechaRegistro(response.getFechaRegistro());
}

    // Obtener respuestas de un comentario con perfil de usuario y reacciones
    public List<DtoResponseComment> getResponsesByCommentWithDetails(String idComentario) {
        List<TResponseComment> responses = repoResponse.findByComentarioIdComentario(idComentario);
    
        return responses.stream().map(response -> {
            DtoResponseComment dto = convertToDto(response);
    
            // Obtener el perfil del usuario
            TUserProfile userProfileEntity = repoUserProfile.findByUsuario(response.getUsuario().getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Perfil de usuario no encontrado"));
    
            dto.setNombreCompleto(userProfileEntity.getNombre() + " " + userProfileEntity.getApellidos());
            dto.setAvatar(userProfileEntity.getFotoPerfil());
    
            // Obtener el resumen de reacciones
            List<DtoReactionSummaryComment> reacciones = repoReactionComment.findByRespuestaIdRespuesta(response.getIdRespuesta())
                    .stream()
                    .collect(Collectors.groupingBy(TReactionComment::getTipo, Collectors.counting()))
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        DtoReactionSummaryComment reactionSummary = new DtoReactionSummaryComment();
                        reactionSummary.setTipo(entry.getKey());
                        reactionSummary.setCantidad(entry.getValue());
                        return reactionSummary;
                    })
                    .collect(Collectors.toList());
            dto.setReacciones(reacciones);
    
            return dto;
        }).collect(Collectors.toList());
    }

    // Obtener respuestas hijas de una respuesta
    public List<DtoResponseComment> getResponsesByParent(String idRespuestaPadre) {
        List<TResponseComment> responses = repoResponse.findByRespuestaPadreIdRespuesta(idRespuestaPadre);
    
        return responses.stream().map(response -> {
            DtoResponseComment dto = convertToDto(response);
    
            // Obtener el perfil del usuario
            TUserProfile userProfileEntity = repoUserProfile.findByUsuario(response.getUsuario().getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Perfil de usuario no encontrado"));
    
            dto.setNombreCompleto(userProfileEntity.getNombre() + " " + userProfileEntity.getApellidos());
            dto.setAvatar(userProfileEntity.getFotoPerfil());
    
            // Obtener el resumen de reacciones
            List<DtoReactionSummaryComment> reacciones = repoReactionComment.findByRespuestaIdRespuesta(response.getIdRespuesta())
                    .stream()
                    .collect(Collectors.groupingBy(TReactionComment::getTipo, Collectors.counting()))
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        DtoReactionSummaryComment reactionSummary = new DtoReactionSummaryComment();
                        reactionSummary.setTipo(entry.getKey());
                        reactionSummary.setCantidad(entry.getValue());
                        return reactionSummary;
                    })
                    .collect(Collectors.toList());
            dto.setReacciones(reacciones);
    
            return dto;
        }).collect(Collectors.toList());
    }

    // Convertir entidad a DTO
    private DtoResponseComment convertToDto(TResponseComment response) {
        DtoResponseComment dto = new DtoResponseComment();
        dto.setIdRespuesta(response.getIdRespuesta());
        dto.setIdComentario(response.getComentario() != null ? response.getComentario().getIdComentario() : null);
        dto.setIdRespuestaPadre(response.getRespuestaPadre() != null ? response.getRespuestaPadre().getIdRespuesta() : null);
        dto.setIdUsuario(response.getUsuario().getIdUsuario());
        dto.setContenido(response.getContenido());
        dto.setFechaRegistro(response.getFechaRegistro());
        dto.setRespuestasHijas(getResponsesByParent(response.getIdRespuesta()));
        return dto;
    }
}
