package foro.Unamba_forum.Entity;

import java.io.Serializable;
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
@Table(name = "reaccionpublicacion")
public class TReactionPublication implements Serializable {
    @Id
    @Column(name = "idReaccion")
    private String idReaccion;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private TUser usuario;

    @ManyToOne
    @JoinColumn(name = "idPublicacion", nullable = false)
    private TPublication publicacion;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "fechaReaccion")
    private Timestamp fechaReaccion;
}
