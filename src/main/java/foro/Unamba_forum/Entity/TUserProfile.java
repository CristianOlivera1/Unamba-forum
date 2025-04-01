package foro.Unamba_forum.Entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "perfilusuario")
public class TUserProfile implements Serializable {
  @Id
  @Column(name = "idPerfil")
  private String idPerfil;

  @OneToOne
  @JoinColumn(name = "idUsuario")
  private TUser idUsuario;

  // esto es para poder usar el usuario en 2 metodos diferentes de RepoUserProfile
  @Column(name = "idUsuario", insertable = false, updatable = false)
  private String usuario;

  @ManyToOne
  @JoinColumn(name = "idCarrera")
  private TCareer idCarrera;

  @Column(name = "nombre")
  private String nombre;

  @Column(name = "apellidos")
  private String apellidos;

  @Column(name = "descripcion", columnDefinition = "TEXT")
  private String descripcion;

  @Column(name = "fotoPerfil")
  private String fotoPerfil;

  @Column(name = "fotoPortada")
  private String fotoPortada;

  @Column(name = "fechaNacimiento")
  private Date fechaNacimiento;

  @Column(name = "genero")
  private Byte genero;

  @Column(name = "fechaActualizacion")
  private Timestamp fechaActualizacion;
}
