package foro.Unamba_forum.Service.Publication.RequestsObject;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestInsertPublication {
    @NotBlank(message = "El campo \"idUsuario \" es requerido")
    private String idUsuario;

    @NotBlank(message = "El campo \"idCategoria \" es requerido")
    private String idCategoria;

    @NotBlank(message = "El campo \"idCarrera \" es requerido")
    private String idCarrera;

    @NotBlank(message = "El campo \"titulo \" es requerido")
    private String titulo;

    @NotBlank(message = "El campo \"contenido \" es requerido")
    private String contenido;

    private List<Object> archivos;
}
