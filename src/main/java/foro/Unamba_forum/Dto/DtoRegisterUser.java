package foro.Unamba_forum.Dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoRegisterUser {
    private String idUsuario;
    private String idCarrera;
	private String idRol;
    private String email;
    private String contrasenha;
    private String nombre;
    private String apellidos;
    private String avatar;
    private Timestamp fechaRegistro;
    private String jwtToken;

    public DtoRegisterUser() {}

    public DtoRegisterUser(String email, String nombre, String apellidos, String avatar, String jwtToken) {
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.avatar = avatar;
        this.jwtToken = jwtToken;
    }
}