package foro.Unamba_forum.Service.CommentPublication.RequestsObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestsInsertCP {
    private String idUsuario;
    private String idPublicacion;
    private String contenido;
}
