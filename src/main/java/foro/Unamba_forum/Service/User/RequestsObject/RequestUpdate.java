package foro.Unamba_forum.Service.User.RequestsObject;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUpdate {
    @NotBlank(message = "El campo \"idUsuario\" es requerido")
    private String idUsuario;
    private String email;
    private String contrasenha;
}
