package foro.Unamba_forum.Business;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoFollowUp;
import foro.Unamba_forum.Entity.TFollowUp;
import foro.Unamba_forum.Entity.TUser;
import foro.Unamba_forum.Entity.TUserProfile;
import foro.Unamba_forum.Repository.RepoFollowUp;
import foro.Unamba_forum.Repository.RepoUser;
import foro.Unamba_forum.Repository.RepoUserProfile;

@Service
public class BusinessFollowUp {
    
    @Autowired
    private RepoFollowUp repoFollowUp;

    @Autowired
    private RepoUser repoUser;

    @Autowired
    private RepoUserProfile repoUserProfile;

    public void followUser(String idSeguidor, String idSeguido) {
        if (idSeguidor.equals(idSeguido)) {
            throw new IllegalArgumentException("Un usuario no puede seguirse a sí mismo.");
        }

        TUser seguidor = repoUser.findById(idSeguidor)
                .orElseThrow(() -> new RuntimeException("Usuario seguidor no encontrado"));
        TUser seguido = repoUser.findById(idSeguido)
                .orElseThrow(() -> new RuntimeException("Usuario seguido no encontrado"));

        TFollowUp followUp = new TFollowUp();
        followUp.setIdSeguimiento(UUID.randomUUID().toString());
        followUp.setSeguidor(seguidor);
        followUp.setSeguido(seguido);
        followUp.setFechaSeguimiento(new Timestamp(System.currentTimeMillis()));

        repoFollowUp.save(followUp);
    }

    public void unfollowUser(String idSeguidor, String idSeguido) {
        TFollowUp followUp = repoFollowUp.findBySeguidorIdUsuarioAndSeguidoIdUsuario(idSeguidor, idSeguido)
                .orElseThrow(() -> new RuntimeException("No se encontró el seguimiento para eliminar"));

        repoFollowUp.delete(followUp);
    }

    public List<DtoFollowUp> getFollowers(String idUsuario) {
        TUser user = repoUser.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<TFollowUp> followers = repoFollowUp.findBySeguido(user);
        return followers.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<DtoFollowUp> getFollowing(String idUsuario) {
        TUser user = repoUser.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<TFollowUp> following = repoFollowUp.findBySeguidor(user);
        return following.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public long countFollowers(String idUsuario) {
        TUser user = repoUser.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return repoFollowUp.countBySeguido(user);
    }

    public long countFollowing(String idUsuario) {
        TUser user = repoUser.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return repoFollowUp.countBySeguidor(user);
    }

   private DtoFollowUp convertToDto(TFollowUp followUp) {
        DtoFollowUp dto = new DtoFollowUp();
        dto.setIdSeguimiento(followUp.getIdSeguimiento());
        dto.setIdSeguidor(followUp.getSeguidor().getIdUsuario());
        dto.setIdSeguido(followUp.getSeguido().getIdUsuario());
        dto.setFechaSeguimiento(followUp.getFechaSeguimiento().toString());

        // Obtener datos del perfil del seguidor
        TUserProfile perfilSeguidor = repoUserProfile.findByIdUsuario(followUp.getSeguidor())
                .orElseThrow(() -> new RuntimeException("Perfil del seguidor no encontrado"));
        dto.setNombreSeguidor(perfilSeguidor.getNombre() + " " + perfilSeguidor.getApellidos());
        dto.setAvatarSeguidor(perfilSeguidor.getFotoPerfil());

        // Obtener datos del perfil del seguido
        TUserProfile perfilSeguido = repoUserProfile.findByIdUsuario(followUp.getSeguido())
                .orElseThrow(() -> new RuntimeException("Perfil del seguido no encontrado"));
        dto.setNombreSeguido(perfilSeguido.getNombre() + " " + perfilSeguido.getApellidos());
        dto.setAvatarSeguido(perfilSeguido.getFotoPerfil());

        return dto;
    }
}
