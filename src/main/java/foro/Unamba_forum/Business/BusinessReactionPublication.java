package foro.Unamba_forum.Business;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoReactionPublication;
import foro.Unamba_forum.Dto.DtoReactionSummary;
import foro.Unamba_forum.Dto.DtoUserProfile;
import foro.Unamba_forum.Entity.TReactionPublication;
import foro.Unamba_forum.Entity.TUserProfile;
import foro.Unamba_forum.Repository.RepoPublication;
import foro.Unamba_forum.Repository.RepoReactionPublication;
import foro.Unamba_forum.Repository.RepoUser;
import foro.Unamba_forum.Repository.RepoUserProfile;

@Service
public class BusinessReactionPublication {
    
    @Autowired
    private RepoReactionPublication repoReaction;

    @Autowired
    private RepoPublication repoPublication;
    @Autowired
    private RepoUser repoUser;

    @Autowired
    private RepoUserProfile repoUserProfile;

    // Agregar una reacción
    public void addReaction(DtoReactionPublication dtoReaction) {
        TReactionPublication reaction = new TReactionPublication();
        reaction.setIdReaccion(UUID.randomUUID().toString());
        reaction.setUsuario(repoUser.findById(dtoReaction.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")));
        reaction.setPublicacion(repoPublication.findById(dtoReaction.getIdPublicacion())
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada")));
        reaction.setTipo(dtoReaction.getTipo());
        reaction.setFechaReaccion(new Timestamp(System.currentTimeMillis()));

        repoReaction.save(reaction);
        dtoReaction.setFechaReaccion(reaction.getFechaReaccion());
        dtoReaction.setIdReaccion(reaction.getIdReaccion());
    }
    
public List<DtoReactionSummary> getReactionSummary(String idPublicacion) {
    List<DtoReactionSummary> summary = List.of(
        createReactionSummary(idPublicacion, "Me identifica"),
        createReactionSummary(idPublicacion, "Es increíble"),
        createReactionSummary(idPublicacion, "Qué divertido")
    );
    return summary;
}

private DtoReactionSummary createReactionSummary(String idPublicacion, String tipo) {
    long cantidad = repoReaction.countByPublicacionIdPublicacionAndTipo(idPublicacion, tipo);
    List<DtoUserProfile> usuarios = repoReaction.findByPublicacionIdPublicacionAndTipo(idPublicacion, tipo)
        .stream()
        .map(reaction -> {
            // Obtener el perfil del usuario
            TUserProfile userProfileEntity = repoUserProfile.findByUsuario(reaction.getUsuario().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Perfil de usuario no encontrado"));

            // Mapear a DtoUserProfile
            DtoUserProfile userProfile = new DtoUserProfile();
            userProfile.setIdPerfil(userProfileEntity.getIdPerfil());
            userProfile.setIdUsuario(reaction.getUsuario().getIdUsuario());
            userProfile.setNombre(userProfileEntity.getNombre());
            userProfile.setApellidos(userProfileEntity.getApellidos());
            userProfile.setFotoPerfil(userProfileEntity.getFotoPerfil());
            userProfile.setIdCarrera(userProfileEntity.getIdCarrera() != null ? userProfileEntity.getIdCarrera().getIdCarrera() : null);

            return userProfile;
        })
        .collect(Collectors.toList());

    DtoReactionSummary summary = new DtoReactionSummary();
    summary.setTipo(tipo);
    summary.setCantidad(cantidad);
    summary.setUsuarios(usuarios);
    return summary;
}

    // Verificar si un usuario ya reaccionó a una publicación
    public boolean hasUserReacted(String idUsuario, String idPublicacion) {
        return repoReaction.existsByUsuarioIdUsuarioAndPublicacionIdPublicacion(idUsuario, idPublicacion);
    }
    // Remover una reacción
    public void removeReaction(String idUsuario, String idPublicacion) {
        repoReaction.deleteByUsuarioIdUsuarioAndPublicacionIdPublicacion(idUsuario, idPublicacion);
    }

    // Obtener la cantidad de reacciones por tipo
    public long getReactionCountByType(String idPublicacion, String tipo) {
        return repoReaction.countByPublicacionIdPublicacionAndTipo(idPublicacion, tipo);
    }

    // Obtener el total de reacciones
    public long getTotalReactions(String idPublicacion) {
        return repoReaction.countByPublicacionIdPublicacion(idPublicacion);
    }

    // Obtener las personas que reaccionaron por tipo
    public List<DtoReactionPublication> getReactionsByType(String idPublicacion, String tipo) {
        List<TReactionPublication> reactions = repoReaction.findByPublicacionIdPublicacionAndTipo(idPublicacion, tipo);
        return reactions.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Convertir entidad a DTO
    private DtoReactionPublication convertToDto(TReactionPublication reaction) {
        DtoReactionPublication dto = new DtoReactionPublication();
        dto.setIdReaccion(reaction.getIdReaccion());
        dto.setIdUsuario(reaction.getUsuario().getIdUsuario());
        dto.setIdPublicacion(reaction.getPublicacion().getIdPublicacion());
        dto.setTipo(reaction.getTipo());
        dto.setFechaReaccion(reaction.getFechaReaccion());
        return dto;
    }
}
