package foro.Unamba_forum.Dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoFollowUp {
    private String idSeguimiento;
    private String idSeguidor;
    private String idSeguido;
    private String nombreSeguidor; 
    private String avatarSeguidor; 
    private String carreraSeguidor;
    private String nombreSeguido;  
    private String avatarSeguido;
    private String carreraSeguido; 
    private Timestamp fechaSeguimiento;
      
}
