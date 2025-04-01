package foro.Unamba_forum.Dto;

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
    private String fechaRegistro;
}