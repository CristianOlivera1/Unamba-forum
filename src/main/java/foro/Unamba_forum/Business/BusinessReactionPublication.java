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
import jakarta.transaction.Transactional;
import foro.Unamba_forum.Entity.TNotification;

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

    @Autowired
    private BusinessNotification notificacionService;

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

       // Crear notificación solo si el usuario que reacciona no es el autor de la publicación
    String idUsuarioPublicacion = reaction.getPublicacion().getUsuario().getIdUsuario();
    if (!idUsuarioPublicacion.equals(dtoReaction.getIdUsuario())) {
        String tipoReaccion = dtoReaction.getTipo();
        String icono = obtenerIcono(tipoReaccion);

        String mensaje = "ha reaccionado con " + tipoReaccion + " " + icono + ", " +
                "a tu publicación sobre " + reaction.getPublicacion().getTitulo() + ".";

        notificacionService.createNotification(
                idUsuarioPublicacion,
                reaction.getUsuario().getIdUsuario(),
                mensaje,
                TNotification.TipoNotificacion.REACCION,
                reaction.getPublicacion().getIdPublicacion());
    }
    }

    public static String obtenerIcono(String tipo) {
        switch (tipo) {
            case "Me identifica":
                return "👌";
            case "Es increíble":
                return "✨";
            case "Qué divertido":
                return "😂";
            default:
                return "❓"; 
        }
    }

    public DtoReactionPublication updateReaction(String idUsuario, String idPublicacion, String nuevoTipo) {
        // Buscar la reacción existente
        TReactionPublication reaction = repoReaction.findByUsuarioIdUsuarioAndPublicacionIdPublicacion(idUsuario, idPublicacion)
                .orElseThrow(() -> new RuntimeException("Reacción no encontrada"));
    
        // Actualizar el tipo de reacción
        reaction.setTipo(nuevoTipo);
        reaction.setFechaReaccion(new Timestamp(System.currentTimeMillis()));
    
        // Guardar los cambios en la base de datos
        repoReaction.save(reaction);
    
        // Convertir la entidad actualizada a DTO y devolverla
        return convertToDto(reaction);
    }

    public List<DtoReactionSummary> getReactionSummary(String idPublicacion) {
        List<DtoReactionSummary> summary = List.of(
                createReactionSummary(idPublicacion, "Me identifica"),
                createReactionSummary(idPublicacion, "Es increíble"),
                createReactionSummary(idPublicacion, "Qué divertido"));
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
                    userProfile.setIdCarrera(
                            userProfileEntity.getIdCarrera() != null ? userProfileEntity.getIdCarrera().getIdCarrera()
                                    : null);

                    return userProfile;
                })
                .collect(Collectors.toList());

        DtoReactionSummary summary = new DtoReactionSummary();
        summary.setTipo(tipo);
        summary.setCantidad(cantidad);
        summary.setUsuarios(usuarios);
        return summary;
    }

    @Transactional
public void deleteReaction(String idUsuario, String idPublicacion) {
    // Buscar la reacción existente
    TReactionPublication reaction = repoReaction.findByUsuarioIdUsuarioAndPublicacionIdPublicacion(idUsuario, idPublicacion)
    .orElse(null);

if (reaction == null) {
System.out.println("No se encontró la reacción con idUsuario: " + idUsuario + " y idPublicacion: " + idPublicacion);
throw new RuntimeException("Reacción no encontrada");
}

    // Eliminar la reacción de la base de datos
    repoReaction.delete(reaction);
}

    // Obtener la reacción actual del usuario en una publicación
    public DtoReactionPublication getReaction(String idUsuario, String idPublicacion) {
        TReactionPublication reaction = repoReaction.findByUsuarioIdUsuarioAndPublicacionIdPublicacion(idUsuario, idPublicacion)
                .orElse(null);

        if (reaction == null) {
            return null;
        }

        return convertToDto(reaction);
    }

    // Verificar si un usuario ya reaccionó a una publicación
    public boolean hasUserReacted(String idUsuario, String idPublicacion) {
        return repoReaction.existsByUsuarioIdUsuarioAndPublicacionIdPublicacion(idUsuario, idPublicacion);
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
