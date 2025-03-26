package foro.Unamba_forum.Dto;

import java.sql.Timestamp;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoFile {
    private String idArchivo;
    private String idPublicacion;
    private String tipo;
    private String rutaArchivo;
    private Timestamp fechaRegistro;
    @JsonIgnore 
    private MultipartFile file;
}
