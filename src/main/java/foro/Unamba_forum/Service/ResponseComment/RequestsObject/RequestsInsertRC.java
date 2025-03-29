package foro.Unamba_forum.Service.ResponseComment.RequestsObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestsInsertRC {
    private String idComentario;
    private String idRespuestaPadre;
    private String idUsuario;
    private String contenido;
}
