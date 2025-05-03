package foro.Unamba_forum.Service.Publication.RequestsObject;

import java.util.List;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUpdatePublication {
    private String idPublicacion;
    private String idCategoria;
    private String titulo;
    private String contenido;
    private List<Object> archivos;
}
