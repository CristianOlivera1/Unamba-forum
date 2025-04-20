package foro.Unamba_forum.Entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "publicacion")
public class TPublication implements Serializable  {
      @Id
    @Column(name = "idPublicacion")
    private String idPublicacion;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private TUser usuario;

    @ManyToOne
    @JoinColumn(name = "idCategoria", nullable = false)
    private TCategory categoria;

    @ManyToOne
    @JoinColumn(name = "idCarrera", nullable = false)
    private TCareer carrera;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "contenido")
    private String contenido;

    @Column(name = "fijada")
    private boolean fijada; 

    @Column(name = "fechaActualizacion")
    private Timestamp fechaActualizacion;

    @Column(name = "fechaRegistro")
    private Timestamp fechaRegistro;
}
