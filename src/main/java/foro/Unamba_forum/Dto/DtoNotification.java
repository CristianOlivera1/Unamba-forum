package foro.Unamba_forum.Dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoNotification {
    private String idNotificacion;
    private String mensaje;
    private String tipo;
    private String idRecurso;
    private boolean leido;
    private String idActor;
    private String nombreActor; 
    private String avatar; 
    private Timestamp fechaRegistro;
    
}
