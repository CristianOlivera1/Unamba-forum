package foro.Unamba_forum.Dto;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class DtoDetailProfile {
    private String rol;
    private String carrera;
    private Timestamp fechaRegistro; 
    private Date fechaNacimiento;
    private Byte genero;
}
