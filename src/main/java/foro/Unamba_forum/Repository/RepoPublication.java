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
  @Query("SELECT p FROM TPublication p WHERE EXISTS (SELECT f FROM TFile f WHERE f.publicacion = p) ORDER BY p.fijada DESC, p.fechaRegistro DESC")
  Page<TPublication> findPublicationsWithFiles(Pageable pageable);

  @Query("SELECT p FROM TPublication p WHERE NOT EXISTS (SELECT f FROM TFile f WHERE f.publicacion = p) ORDER BY p.fijada DESC, p.fechaRegistro DESC")
  Page<TPublication> findPublicationsWithoutFiles(Pageable pageable);

  @Query("SELECT p FROM TPublication p WHERE (p.carrera.idCarrera = :idCarrera OR p.categoria.idCategoria = :idCategoria) AND p.idPublicacion != :excludeIdPublicacion")
  Page<TPublication> findRelatedPublications(@Param("idCarrera") String idCarrera,
      @Param("idCategoria") String idCategoria, @Param("excludeIdPublicacion") String excludeIdPublicacion,
      Pageable pageable);

  @Query("SELECT p FROM TPublication p WHERE p.carrera.idCarrera = :idCarrera AND EXISTS (SELECT f FROM TFile f WHERE f.publicacion = p)")
  List<TPublication> findByCarreraId(@Param("idCarrera") String idCarrera);

  @Query("SELECT p FROM TPublication p WHERE p.carrera.idCarrera = :idCarrera AND EXISTS (SELECT f FROM TFile f WHERE f.publicacion = p) ORDER BY p.fijada DESC, p.fechaRegistro DESC")
  Page<TPublication> findPublicationsWithFilesByCareer(@Param("idCarrera") String idCarrera, Pageable pageable);

  @Query("SELECT p FROM TPublication p WHERE p.carrera.idCarrera = :idCarrera AND NOT EXISTS (SELECT f FROM TFile f WHERE f.publicacion = p) ORDER BY p.fijada DESC, p.fechaRegistro DESC")
  Page<TPublication> findPublicationsWithoutFilesByCareer(@Param("idCarrera") String idCarrera, Pageable pageable);

  @Query("SELECT p FROM TPublication p WHERE p.usuario.idUsuario = :idUsuario ORDER BY p.fechaRegistro DESC")
  Page<TPublication> findByUsuarioIdOrderByFechaRegistroDesc(@Param("idUsuario") String idUsuario, Pageable pageable);

Page<TPublication> findByIdPublicacionNotAndCarreraIdCarreraOrIdPublicacionNotAndCategoriaIdCategoriaOrderByFechaRegistroDesc(
    String idPublicacion1, String idCarrera, String idPublicacion2, String idCategoria, Pageable pageable);

        //buscador de publicaciones
@Query("SELECT p FROM TPublication p " +
       "JOIN p.usuario u " +
       "JOIN u.perfil up " +
       "JOIN p.carrera c " +
       "WHERE LOWER(p.titulo) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "   OR LOWER(p.contenido) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "   OR LOWER(up.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "   OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :query, '%'))")
Page<TPublication> searchPublications(@Param("query") String query, Pageable pageable);
}
