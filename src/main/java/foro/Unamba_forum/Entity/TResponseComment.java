package foro.Unamba_forum.Entity;

import jakarta.persistence.Table;

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
@Table(name = "respuestacomentario")
public class TResponseComment {
    
    @Id
    @Column(name = "idRespuesta", nullable = false, length = 36)
    private String idRespuesta;

    @ManyToOne
    @JoinColumn(name = "idComentario", referencedColumnName = "idComentario", nullable = true)
    private TCommentPublication comentario;

    @ManyToOne
    @JoinColumn(name = "idRespuestaPadre", referencedColumnName = "idRespuesta", nullable = true)
    private TResponseComment respuestaPadre;

    @ManyToOne
    @JoinColumn(name = "idUsuario", referencedColumnName = "idUsuario", nullable = false)
    private TUser usuario;

    @Column(name = "contenido")
    private String contenido;

    @Column(name = "fechaRegistro")
    private Timestamp fechaRegistro;
}

