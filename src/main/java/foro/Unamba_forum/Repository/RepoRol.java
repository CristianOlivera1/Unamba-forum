package foro.Unamba_forum.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import foro.Unamba_forum.Entity.TRol;

public interface RepoRol extends JpaRepository<TRol, String> {
@Query("SELECT r FROM TRol r WHERE r.tipo = :tipo")
Optional<TRol> findByTipo(@Param("tipo") TRol.TipoRol tipo);
}
