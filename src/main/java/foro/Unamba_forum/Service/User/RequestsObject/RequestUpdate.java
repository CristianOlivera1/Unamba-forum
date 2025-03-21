package foro.Unamba_forum.Service.User.RequestsObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUpdate {
    private String idUsuario;
    private String email;
    private String contrasenha;
}
