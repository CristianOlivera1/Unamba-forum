package foro.Unamba_forum.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoFollowUp {
    private String idSeguimiento;
    private String idSeguidor;
    private String idSeguido;
    private String fechaSeguimiento;
    private String nombreSeguidor; 
    private String nombreSeguido;  
    private String avatarSeguidor; 
    private String avatarSeguido;  
}
