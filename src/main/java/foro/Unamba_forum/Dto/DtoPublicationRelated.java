package foro.Unamba_forum.Dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class DtoPublicationRelated {
    private String idPublicacion;
    private String idUsuario;
    private String idCategoria;
    private String idCarrera;
    private String titulo;
    private String contenido;
    private Timestamp fechaRegistro;
    private String nombreCompleto;
    private long totalCommentarios;
    private long totalReacciones;
    private List<DtoFile> archivos;
}
