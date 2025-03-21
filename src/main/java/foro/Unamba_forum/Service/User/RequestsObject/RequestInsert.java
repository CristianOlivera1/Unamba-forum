package foro.Unamba_forum.Service.User.RequestsObject;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestInsert {
    @NotBlank(message = "El campo \"Email \" es requerido")
    private String email ;

    @NotBlank(message = "El campo \"Contrasenha\" es requerido")
    private String contrasenha ;
}
