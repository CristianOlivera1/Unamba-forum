package foro.Unamba_forum.Dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoUserProfileHover {
    private String idUsuario;
    private String idCarrera;
    private String fotoPerfil;
    private String fotoPortada;
    private String nombreCompleto;
    private String descripcion;
    private long totalFollowers;
    private long totalFollowing;
    private String rol;
    private String carrera;
    private Timestamp fechaRegistro; 
}
