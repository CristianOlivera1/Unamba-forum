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
@Table(name = "comentario")
public class TCommentPublication implements Serializable{
    
    @Id
    @Column(name = "idComentario")
    private String idComentario;

    @ManyToOne
    @JoinColumn(name = "idUsuario", referencedColumnName = "idUsuario", nullable = true)
    private TUser usuario;

    @ManyToOne
    @JoinColumn(name = "idPublicacion", referencedColumnName = "idPublicacion", nullable = true)
    private TPublication publicacion;

    @Column(name = "contenido", columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fechaRegistro")
    private Timestamp fechaRegistro;

    @Column(name = "fechaActualizacion")
    private Timestamp fechaActualizacion;
}
