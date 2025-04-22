package foro.Unamba_forum.Dto;

import java.sql.Timestamp;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoResponseComment {
    private String idRespuesta;
    private String idComentario;
    private String idRespuestaPadre; // NULL si es respuesta directa al comentario y se llena en caso de que sea una respuesta a una respuesta
    private String idUsuario;
    private String contenido;
    private Timestamp fechaRegistro;
    private List<DtoResponseComment> respuestasHijas; // Respuestas a respuestas
    private String nombreCompleto;
    private String avatar;
    private List<DtoReactionSummaryComment> reacciones;

}
