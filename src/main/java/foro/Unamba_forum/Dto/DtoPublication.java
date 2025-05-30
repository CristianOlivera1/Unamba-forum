package foro.Unamba_forum.Dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoPublication {
    private String idPublicacion;
    private String idUsuario;
    private String idCategoria;
    private String idCarrera;
    private String titulo;
    private String contenido;
    private boolean fijada; 
	private String tipoRol;
    private Timestamp fechaRegistro;
    private Timestamp fechaActualizacion;
    private String nombreCompleto;
    private String avatar;
    private String nombreCarrera;
    private String nombreCategoria;
    private List<DtoFile> archivos;
}
