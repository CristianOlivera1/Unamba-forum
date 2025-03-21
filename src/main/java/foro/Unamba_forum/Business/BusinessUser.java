package foro.Unamba_forum.Business;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoUser;
import foro.Unamba_forum.Entity.TUser;
import foro.Unamba_forum.Helper.AesUtil;
import foro.Unamba_forum.Helper.JwtUtil;
import foro.Unamba_forum.Repository.RepoUser;
import jakarta.transaction.Transactional;


@Service
public class BusinessUser {
    @Autowired
    private RepoUser repoUser;

    @Transactional
    public void insert(DtoUser dtoUser) throws Exception {
        dtoUser.setIdUsuario(UUID.randomUUID().toString());
        dtoUser.setFechaRegistro(new Timestamp(new Date().getTime()));
        dtoUser.setFechaActualizacion(new Timestamp(new Date().getTime()));

        TUser tUser = new TUser();
        tUser.setIdUsuario(dtoUser.getIdUsuario());
        tUser.setEmail(dtoUser.getEmail());
        tUser.setContrasenha(AesUtil.encrypt(dtoUser.getContrasenha()));
        tUser.setFechaRegistro(dtoUser.getFechaRegistro());
        tUser.setFechaActualizacion(dtoUser.getFechaActualizacion());

        repoUser.save(tUser);
    }

    public boolean emailExists(String email) {
        return repoUser.findByEmail(email).isPresent();
    }

    public boolean logout(String idUsuario) {
        Optional<TUser> tUser = repoUser.findById(idUsuario);

        if (!tUser.isPresent()) {
            return false;
        }

        return true;
    }

    public DtoUser login(String email, String contrasenha) throws Exception {
        Optional<TUser> tUserOptional = repoUser.findByEmail(email);

        if (!tUserOptional.isPresent()) {
            return null;
        }

        TUser tUser = tUserOptional.get();
        DtoUser dtoUser = new DtoUser();
        if (!AesUtil.decrypt(tUser.getContrasenha()).equals(contrasenha)) {
            return null;
        }
        dtoUser.setIdUsuario(tUser.getIdUsuario());
        dtoUser.setEmail(tUser.getEmail());
        dtoUser.setJwtToken(new JwtUtil().generateToken(dtoUser.getIdUsuario(), dtoUser.getEmail()));

        return dtoUser;
    }

    public DtoUser getUserById(String idUsuario) {
        Optional<TUser> tUser = repoUser.findById(idUsuario);

        if (!tUser.isPresent()) {
            return null;
        }

        TUser User = tUser.get();
        DtoUser dtoUser = new DtoUser();

        dtoUser.setIdUsuario(User.getIdUsuario());
        dtoUser.setEmail(User.getEmail());
        dtoUser.setContrasenha(User.getContrasenha());
        dtoUser.setFechaRegistro(User.getFechaRegistro());
        dtoUser.setFechaActualizacion(User.getFechaActualizacion());

        return dtoUser;
    }

    public List<DtoUser> getAll() {
        List<TUser> listTUser = repoUser.findAll();
        List<DtoUser> listDtoUser = new ArrayList<>();

        for (TUser item : listTUser) {
            DtoUser dtoUser = new DtoUser();

            dtoUser.setIdUsuario(item.getIdUsuario());
            dtoUser.setEmail(item.getEmail());
            dtoUser.setContrasenha(item.getContrasenha());
            dtoUser.setFechaRegistro(item.getFechaRegistro());
            dtoUser.setFechaActualizacion(item.getFechaActualizacion());

            listDtoUser.add(dtoUser);
        }

        return listDtoUser;
    }

    @Transactional
    public boolean update(DtoUser dtoUser) {
        Optional<TUser> tUsers = repoUser.findById(dtoUser.getIdUsuario());

        if (!tUsers.isPresent()) {
            return false;
        }

        TUser tUser = tUsers.get();
        tUser.setEmail(dtoUser.getEmail());
        tUser.setContrasenha(dtoUser.getContrasenha());
        tUser.setFechaActualizacion(new Timestamp(new Date().getTime()));

        repoUser.save(tUser);

        return true;
    }

    @Transactional
    public boolean delete(String idUsuario) {
        Optional<TUser> tUser = repoUser.findById(idUsuario);

        if (tUser.isPresent()) {
            repoUser.deleteById(idUsuario);
            return true;
        }

        return false;
    }
}
