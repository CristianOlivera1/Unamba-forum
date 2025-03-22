package foro.Unamba_forum.Service.UserProfile.RequestObject;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestInsertUserProfile {
    @NotBlank(message = "El campo \"idUsuario \" es requerido")
    private String idUsuario;

    @NotBlank(message = "El campo \"idCarrera \" es requerido")
    private String idCarrera;
    
    @NotBlank(message = "El campo \"Nombre \" es requerido")
    private String nombre;

    @NotBlank(message = "El campo \"Apellidos \" es requerido")
    private String apellidos;

     private MultipartFile fotoPerfil;
    private MultipartFile fotoPortada;

    @NotBlank(message = "El campo \"Fecha de nacimiento \" es requerido")
    private Date fechaNacimiento;
    private Byte genero;
}
