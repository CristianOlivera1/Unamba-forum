package foro.Unamba_forum.Service.ReactionComment.ResquestsObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestsInsertReactionC {
    private String idComentario;
    private String idRespuesta;
    private String idUsuario;
    private String tipo;
}
