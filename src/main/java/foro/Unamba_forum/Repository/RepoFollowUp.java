package foro.Unamba_forum.Repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import foro.Unamba_forum.Entity.TFollowUp;
import foro.Unamba_forum.Entity.TUser;

@Repository
public interface RepoFollowUp extends JpaRepository<TFollowUp, String> {
    Optional<TFollowUp> findBySeguidorIdUsuarioAndSeguidoIdUsuario(String idSeguidor, String idSeguido);
    List<TFollowUp> findBySeguido(TUser seguido);
    List<TFollowUp> findBySeguidor(TUser seguidor);
    long countBySeguido(TUser seguido);
    long countBySeguidor(TUser seguidor);
}
