package foro.Unamba_forum.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import foro.Unamba_forum.Entity.TPublication;

@Repository
public interface RepoPublication extends JpaRepository<TPublication, String> {
  @Query("SELECT p FROM TPublication p WHERE EXISTS (SELECT f FROM TFile f WHERE f.publicacion = p)")
  Page<TPublication> findPublicationsWithFiles(Pageable pageable);

  @Query("SELECT p FROM TPublication p WHERE NOT EXISTS (SELECT f FROM TFile f WHERE f.publicacion = p)")
  Page<TPublication> findPublicationsWithoutFiles(Pageable pageable);

  @Query("SELECT p FROM TPublication p WHERE (p.carrera.idCarrera = :idCarrera OR p.categoria.idCategoria = :idCategoria) AND p.idPublicacion != :excludeIdPublicacion")
  Page<TPublication> findRelatedPublications(@Param("idCarrera") String idCarrera,
      @Param("idCategoria") String idCategoria, @Param("excludeIdPublicacion") String excludeIdPublicacion,
      Pageable pageable);


    @Query("SELECT p FROM TPublication p WHERE p.carrera.idCarrera = :idCarrera AND EXISTS (SELECT f FROM TFile f WHERE f.publicacion = p)")
    List<TPublication> findByCarreraId(@Param("idCarrera") String idCarrera);

    @Query("SELECT p FROM TPublication p WHERE p.carrera.idCarrera = :idCarrera AND EXISTS (SELECT f FROM TFile f WHERE f.publicacion = p)")
    Page<TPublication> findPublicationsWithFilesByCareer(@Param("idCarrera") String idCarrera, Pageable pageable);

    @Query("SELECT p FROM TPublication p WHERE p.carrera.idCarrera = :idCarrera AND NOT EXISTS (SELECT f FROM TFile f WHERE f.publicacion = p)")
    Page<TPublication> findPublicationsWithoutFilesByCareer(@Param("idCarrera") String idCarrera, Pageable pageable);

    @Query("SELECT p FROM TPublication p WHERE p.carrera.idCarrera = :idCarrera AND NOT EXISTS (SELECT a FROM TFile a WHERE a.publicacion = p)")
List<TPublication> findPublicationsWithoutFilesByCareer2(@Param("idCarrera") String idCarrera);
}
