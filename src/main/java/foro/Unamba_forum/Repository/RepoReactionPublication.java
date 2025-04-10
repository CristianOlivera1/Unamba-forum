package foro.Unamba_forum.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import foro.Unamba_forum.Entity.TReactionPublication;

@Repository
public interface RepoReactionPublication  extends JpaRepository<TReactionPublication, String> {
    
    List<TReactionPublication> findByPublicacionIdPublicacionAndTipo(String idPublicacion, String tipo);

    List<TReactionPublication> findByPublicacionIdPublicacion(String idPublicacion);

    long countByPublicacionIdPublicacionAndTipo(String idPublicacion, String tipo);

    long countByPublicacionIdPublicacion(String idPublicacion);

    void deleteByUsuarioIdUsuarioAndPublicacionIdPublicacion(String idUsuario, String idPublicacion);

    boolean existsByUsuarioIdUsuarioAndPublicacionIdPublicacion(String idUsuario, String idPublicacion);


    Optional<TReactionPublication> findByUsuarioIdUsuarioAndPublicacionIdPublicacion(String idUsuario, String idPublicacion);

}
