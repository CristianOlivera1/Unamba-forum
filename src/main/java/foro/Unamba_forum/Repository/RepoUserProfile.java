package foro.Unamba_forum.Repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import foro.Unamba_forum.Entity.TUser;
import foro.Unamba_forum.Entity.TUserProfile;

@Repository
public interface RepoUserProfile extends JpaRepository<TUserProfile, String> {
    Optional<TUserProfile> findByIdUsuario(TUser idUsuario);

     // Método para buscar por ID en formato String
     Optional<TUserProfile> findByUsuario(String idUsuario);

    @Query(value = "SELECT * FROM perfilusuario ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<TUserProfile> findRandomUsers(int count);
}
