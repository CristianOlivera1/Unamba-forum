package foro.Unamba_forum.Entity;

import java.io.Serializable;
import java.sql.Timestamp;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "nota")
public class TNote implements Serializable {
    @Id
    @Column(name = "idNota")
    private String idNota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario", nullable = false)
    private TUser usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCarrera", nullable = false)
    private TCareer carrera;

    @Column(name = "contenido", columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "backgroundColor")
    private String backgroundColor;

    @Column(name = "radialGradient", columnDefinition = "TEXT")
    private String radialGradient;

    @Column(name = "fechaRegistro")
    private Timestamp fechaRegistro;
}
