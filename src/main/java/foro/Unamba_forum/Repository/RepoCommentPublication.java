package foro.Unamba_forum.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import foro.Unamba_forum.Entity.TCommentPublication;

@Repository
public interface RepoCommentPublication extends JpaRepository<TCommentPublication, String>{
    List<TCommentPublication> findByPublicacionIdPublicacion(String idPublicacion);
    long countByPublicacionIdPublicacion(String idPublicacion);
}
