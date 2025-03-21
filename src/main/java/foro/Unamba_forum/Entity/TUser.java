package foro.Unamba_forum.Entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "usuario")
public class TUser implements Serializable {
    @Id
    @Column(name = "idUsuario")
    private String idUsuario;

    @Column(name = "email")
    private String email;

    @Column(name = "contrasenha")
    private String contrasenha; 

    @Column(name = "fechaRegistro")
    private Timestamp fechaRegistro;

    @Column(name = "fechaActualizacion")
    private Timestamp fechaActualizacion;
    
}