package foro.Unamba_forum.Dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCommentPublication {
    private String idComentario;
    private String idUsuario;
    private String idPublicacion;
    private String contenido;
    private Timestamp fechaRegistro;
    private Timestamp fechaActualizacion;
    private List<DtoReactionSummaryComment> reacciones;
    private long numeroRespuestas;
    private String nombreCompleto; 
    private String avatar;

}
