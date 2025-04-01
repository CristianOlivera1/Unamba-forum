package foro.Unamba_forum.Dto;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoUser {
	private String idUsuario;
	private String idRol;
	private String email;
	private String contrasenha;
	private Timestamp fechaRegistro;
	private Timestamp fechaActualizacion;
	private String jwtToken;
}