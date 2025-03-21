package foro.Unamba_forum.Entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "categoria")

public class TCategory implements Serializable{
    @Id
    @Column(name = "idCategoria")
    private String idCategoria;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "fechaRegistro")
    private Timestamp fechaRegistro;

}
