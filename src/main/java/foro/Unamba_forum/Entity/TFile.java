package foro.Unamba_forum.Entity;

import jakarta.persistence.Table;

import java.io.Serializable;
import java.sql.Timestamp;

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
@Table(name = "archivo")
public class TFile  implements Serializable  {
    
    @Id
    @Column(name = "idArchivo")
    private String idArchivo;

    @ManyToOne
    @JoinColumn(name = "idPublicacion", nullable = false)
    private TPublication publicacion;

    @Column(name = "tipo")
    private String tipo;
    
    @Column(name = "rutaArchivo")
    private String rutaArchivo;

    @Column(name = "fechaRegistro")
    private Timestamp fechaRegistro;
}
