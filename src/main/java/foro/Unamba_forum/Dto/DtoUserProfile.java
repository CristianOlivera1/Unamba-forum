package foro.Unamba_forum.Dto;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoUserProfile {
    private String idPerfil;
    private String idUsuario;
    private String idCarrera;
    private String nombre;
    private String apellidos;
    private String descripcion;
    private String fotoPerfil;
    private String fotoPortada;
    private Date fechaNacimiento;
    private Byte genero;
    private Timestamp fechaActualizacion;
}
