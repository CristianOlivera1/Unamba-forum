package foro.Unamba_forum.Dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoReactionComment {
    private String idReaccion;
    private String idComentario;// Null si es reacción a respuesta
    private String idRespuesta; // Null si es reacción a comentario
    private String idUsuario;
    private String tipo;
    private Timestamp fechaReaccion;
}
