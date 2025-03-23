package foro.Unamba_forum.Service.UserProfile.RequestObject;

import java.sql.Timestamp;
import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUpdateUserProfile {
    @NotBlank(message = "El campo \"idPerfil\" es requerido")
    private String idPerfil;

    @NotBlank(message = "El campo \"idUsuario\" es requerido")
    private String idUsuario;

    @NotBlank(message = "El campo \"idCarrera\" es requerido")
    private String idCarrera;
    
    private String nombre;
    private String apellidos;
    private MultipartFile fotoPerfil;
    private MultipartFile fotoPortada;
    private Date fechaNacimiento;
    private Byte genero;
    private Timestamp fechaActualizacion;

}
