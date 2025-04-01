package foro.Unamba_forum.Dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoRol {
    private String idRol;
	private String tipo;
	private Timestamp fechaRegistro;
}
