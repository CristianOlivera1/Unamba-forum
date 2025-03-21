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
@Table(name = "carrera")
public class TCareer implements Serializable {
    @Id
    @Column(name = "idCarrera")
    private String idCarrera;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "fechaRegistro")
    private Timestamp fechaRegistro;
}
