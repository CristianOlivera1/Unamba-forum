package foro.Unamba_forum.Dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoReactionPublication {
    private String idReaccion;
    private String idUsuario;
    private String idPublicacion;
    private String tipo; 
    private Timestamp fechaReaccion;
}
