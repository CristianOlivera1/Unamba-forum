package foro.Unamba_forum.Business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoRol;
import foro.Unamba_forum.Entity.TRol;
import foro.Unamba_forum.Entity.TUser;
import foro.Unamba_forum.Repository.RepoUser;

@Service
public class BusinessRol {
    @Autowired
private RepoUser repoUser;

public DtoRol getRolByUserId(String idUsuario) {
    // Buscar el usuario por su ID
    TUser user = repoUser.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // Obtener el rol del usuario
    TRol rol = user.getRol();
    if (rol == null) {
        throw new RuntimeException("El usuario no tiene un rol asignado");
    }

    // Convertir el rol a DtoRol
    DtoRol dtoRol = new DtoRol();
    dtoRol.setIdRol(rol.getIdRol());
    dtoRol.setTipo(rol.getTipo().name());
    dtoRol.setFechaRegistro(rol.getFechaRegistro());

    return dtoRol;
}
    
}
