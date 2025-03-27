package foro.Unamba_forum.Service.ReactionPublication.RequestsObject;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestInsertRP {
    @NotBlank(message = "El campo \"idUsuario \" es requerido")
    private String idUsuario;

    @NotBlank(message = "El campo \"idPublicacion \" es requerido")
    private String idPublicacion;

    @NotBlank(message = "El campo \"tipo\" es requerido")
    private String tipo;

}
