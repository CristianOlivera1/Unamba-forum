package foro.Unamba_forum.Entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "notificacion")
public class TNotification implements Serializable {
  @Id
  private String idNotificacion;

  @ManyToOne
  @JoinColumn(name = "idUsuario")
  private TUser usuario;

  @ManyToOne
  @JoinColumn(name = "idUsuarioActor", nullable = false) // Usuario que generó la acción
  private TUser actor;

  private String mensaje;

  @Enumerated(EnumType.STRING)
  private TipoNotificacion tipo;

  private String idRecurso;

  private boolean leido;

  private Timestamp fechaRegistro;

  public enum TipoNotificacion {
    SEGUIMIENTO, REACCION, COMENTARIO,BIENVENIDA
}
}