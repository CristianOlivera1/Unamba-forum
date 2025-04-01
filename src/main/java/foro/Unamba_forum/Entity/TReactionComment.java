package foro.Unamba_forum.Entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
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
@Table(name = "reaccioncomentario")
public class TReactionComment {
    
    @Id
    @Column(name = "idReaccion")
    private String idReaccion;

    @ManyToOne
    @JoinColumn(name = "idComentario", referencedColumnName = "idComentario", nullable = true)
    private TCommentPublication comentario;

    @ManyToOne
    @JoinColumn(name = "idRespuesta", referencedColumnName = "idRespuesta", nullable = true)
    private TResponseComment respuesta;

    @ManyToOne
    @JoinColumn(name = "idUsuario", referencedColumnName = "idUsuario", nullable = true)
    private TUser usuario;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "fechaReaccion")
    private Timestamp fechaReaccion;
}
