package foro.Unamba_forum.Repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import foro.Unamba_forum.Entity.TUserProfile;

@Repository
public interface RepoUserProfile extends JpaRepository<TUserProfile, String> {
}
