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
@Table(name = "seguimiento")
public class TFollowUp {
    @Id
    @Column(name = "idSeguimiento")
    private String idSeguimiento;

    @ManyToOne
    @JoinColumn(name = "idSeguidor", nullable = false)
    private TUser seguidor;

    @ManyToOne
    @JoinColumn(name = "idSeguido", nullable = false)
    private TUser seguido;

    private Timestamp fechaSeguimiento;
}
