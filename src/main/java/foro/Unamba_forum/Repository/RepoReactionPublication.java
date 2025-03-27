package foro.Unamba_forum.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import foro.Unamba_forum.Entity.TReactionPublication;

@Repository
public interface RepoReactionPublication  extends JpaRepository<TReactionPublication, String> {
    
}
