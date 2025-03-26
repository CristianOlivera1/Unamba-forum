package foro.Unamba_forum.Service.Publication.RequestsObject;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUpdatePublication {
    private String idPublicacion;
    private String idCategoria;
    private String titulo;
    private String contenido;
    private List<MultipartFile> archivos;
}
