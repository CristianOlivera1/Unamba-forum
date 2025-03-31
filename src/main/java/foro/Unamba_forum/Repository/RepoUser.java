package foro.Unamba_forum.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import foro.Unamba_forum.Entity.TUser;

@Repository
public interface RepoUser extends JpaRepository<TUser, String> {

     //esto es para validar por independiente los datos en el formulario
   Optional<TUser> findByEmail(String email);
}