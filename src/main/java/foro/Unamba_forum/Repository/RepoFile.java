package foro.Unamba_forum.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import foro.Unamba_forum.Entity.TFile;
import foro.Unamba_forum.Entity.TPublication;

@Repository
public interface RepoFile extends JpaRepository<TFile, String> {
        List<TFile> findByPublicacion(TPublication publicacion);

}
