package foro.Unamba_forum.Business;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoUserProfile;
import foro.Unamba_forum.Entity.TCareer;
import foro.Unamba_forum.Entity.TUser;
import foro.Unamba_forum.Entity.TUserProfile;
import foro.Unamba_forum.Repository.RepoCareer;
import foro.Unamba_forum.Repository.RepoUser;
import foro.Unamba_forum.Repository.RepoUserProfile;
import jakarta.transaction.Transactional;

@Service
public class BusinessUserProfile {
    @Autowired
    private RepoUserProfile repoUserProfile;

    @Autowired
    private RepoUser repoUser;

    @Autowired
    private RepoCareer repoCareer;

    @Transactional
    public void insert(DtoUserProfile dtoUserProfile) {

        dtoUserProfile.setIdPerfil(UUID.randomUUID().toString());
        dtoUserProfile.setFechaActualizacion(new Timestamp(new Date().getTime()));

        TUserProfile tUserProfile = new TUserProfile();
        tUserProfile.setIdPerfil(dtoUserProfile.getIdPerfil());
        tUserProfile.setNombre(dtoUserProfile.getNombre());
        tUserProfile.setApellidos(dtoUserProfile.getApellidos());
        tUserProfile.setFotoPerfil(dtoUserProfile.getFotoPerfil());
        tUserProfile.setFotoPortada(dtoUserProfile.getFotoPortada());
        tUserProfile.setFechaNacimiento(dtoUserProfile.getFechaNacimiento());
        tUserProfile.setGenero(dtoUserProfile.getGenero());
        tUserProfile.setFechaActualizacion(dtoUserProfile.getFechaActualizacion());
        
        // Obtener el usuario por ID y establecerlo en el perfil
        Optional<TUser> usuario = repoUser.findById(dtoUserProfile.getIdUsuario());
        if (usuario.isPresent()) {
            tUserProfile.setUsuario(usuario.get());
        } else {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Obtener la carrera por ID y establecerla en el perfil
        Optional<TCareer> carrera = repoCareer.findById(dtoUserProfile.getIdCarrera());
        if (carrera.isPresent()) {
            tUserProfile.setCarrera(carrera.get());
        } else {
            throw new RuntimeException("Carrera no encontrada");
        }

        repoUserProfile.save(tUserProfile);
    }

    public DtoUserProfile getById(String idPerfil) {
        Optional<TUserProfile> tUserProfile = repoUserProfile.findById(idPerfil);
        if (tUserProfile.isPresent()) {
            TUserProfile profile = tUserProfile.get();
            DtoUserProfile dtoUserProfile = new DtoUserProfile();
            dtoUserProfile.setIdPerfil(profile.getIdPerfil());
            dtoUserProfile.setNombre(profile.getNombre());
            dtoUserProfile.setApellidos(profile.getApellidos());
            dtoUserProfile.setFotoPerfil(profile.getFotoPerfil());
            dtoUserProfile.setFotoPortada(profile.getFotoPortada());
            dtoUserProfile.setFechaNacimiento(profile.getFechaNacimiento());
            dtoUserProfile.setGenero(profile.getGenero());
            dtoUserProfile.setFechaActualizacion(profile.getFechaActualizacion());
            // Asignar las relaciones
            // dtoUserProfile.setIdUsuario(profile.getUsuario().getIdUsuario());
            // dtoUserProfile.setIdCarrera(profile.getCarrera().getIdCarrera());

            return dtoUserProfile;
        }
        return null;
    }

    @Transactional
    public void update(DtoUserProfile dtoUserProfile) {
        Optional<TUserProfile> tUserProfile = repoUserProfile.findById(dtoUserProfile.getIdPerfil());
        if (tUserProfile.isPresent()) {
            TUserProfile profile = tUserProfile.get();
            profile.setNombre(dtoUserProfile.getNombre());
            profile.setApellidos(dtoUserProfile.getApellidos());
            profile.setFotoPerfil(dtoUserProfile.getFotoPerfil());
            profile.setFotoPortada(dtoUserProfile.getFotoPortada());
            profile.setFechaNacimiento(dtoUserProfile.getFechaNacimiento());
            profile.setGenero(dtoUserProfile.getGenero());
            profile.setFechaActualizacion(dtoUserProfile.getFechaActualizacion());
            // Asignar las relaciones
            // profile.setUsuario(...);
            // profile.setCarrera(...);

            repoUserProfile.save(profile);
        }
    }

    @Transactional
    public void delete(String idPerfil) {
        repoUserProfile.deleteById(idPerfil);
    }
}
