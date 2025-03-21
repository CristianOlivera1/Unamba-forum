package foro.Unamba_forum.Entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "perfilusuario")
public class TUserProfile implements Serializable{
     @Id
    private String idPerfil;
    
    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private TUser usuario;
    
    @ManyToOne
    @JoinColumn(name = "idCarrera")
    private TCareer carrera;
    
    private String nombre;
    private String apellidos;
    private String fotoPerfil;
    private String fotoPortada;
    private Date fechaNacimiento;
    private Byte genero;
    private Timestamp fechaActualizacion;
}
