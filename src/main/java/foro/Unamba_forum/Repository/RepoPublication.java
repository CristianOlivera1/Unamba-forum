package foro.Unamba_forum.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import foro.Unamba_forum.Entity.TPublication;

@Repository
public interface RepoPublication extends JpaRepository<TPublication, String> {
  @Query("SELECT p FROM TPublication p WHERE EXISTS (SELECT f FROM TFile f WHERE f.publicacion = p)")
    Page<TPublication> findPublicationsWithFiles(Pageable pageable);

    @Query("SELECT p FROM TPublication p WHERE NOT EXISTS (SELECT f FROM TFile f WHERE f.publicacion = p)")
    Page<TPublication> findPublicationsWithoutFiles(Pageable pageable);

}
