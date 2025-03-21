package foro.Unamba_forum.Dto;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCareer {
    private String idCarrera;
    private String nombre;
    private Timestamp fechaRegistro;

}
