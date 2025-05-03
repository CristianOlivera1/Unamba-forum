package foro.Unamba_forum.Dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoNote {
    private String idNota;
    private String nombreCompleto;
    private String nombreCarrera;
    private String avatar;
    private String contenido;
    private String backgroundColor;
    private String radialGradient;
    private Timestamp fechaPublicacion;
}
