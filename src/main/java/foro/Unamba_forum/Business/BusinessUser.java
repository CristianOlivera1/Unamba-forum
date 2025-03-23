package foro.Unamba_forum.Business;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoRegisterUser;
import foro.Unamba_forum.Dto.DtoUser;
import foro.Unamba_forum.Entity.TCareer;

import foro.Unamba_forum.Entity.TUser;
import foro.Unamba_forum.Entity.TUserProfile;
import foro.Unamba_forum.Helper.AesUtil;
import foro.Unamba_forum.Helper.JwtUtil;
import foro.Unamba_forum.Helper.Validation;
import foro.Unamba_forum.Repository.RepoCareer;
import foro.Unamba_forum.Repository.RepoUser;
import foro.Unamba_forum.Repository.RepoUserProfile;
import jakarta.transaction.Transactional;

@Service
public class BusinessUser {
    @Autowired
    private RepoUser repoUser;
    @Autowired
    private RepoUserProfile repoUserProfile;
    @Autowired
    private RepoCareer repoCareer;
    @Autowired
    private SupabaseStorageService supabaseStorageService;

    @Transactional
    public void insert(DtoUser dtoUser) throws Exception {
        dtoUser.setIdUsuario(UUID.randomUUID().toString());
        dtoUser.setFechaRegistro(new Timestamp(System.currentTimeMillis()));
        dtoUser.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

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

    @Transactional
    public void registrarUsuario(DtoRegisterUser dto) throws Exception {
        // Crear el usuario
        TUser usuario = new TUser();
        usuario.setIdUsuario(UUID.randomUUID().toString());
        usuario.setEmail(dto.getEmail());
        String contrasenhaEncriptada = AesUtil.encrypt(dto.getContrasenha());
        usuario.setContrasenha(contrasenhaEncriptada);
        usuario.setFechaRegistro(new Timestamp(System.currentTimeMillis()));

        repoUser.save(usuario);

        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setFechaRegistro(usuario.getFechaRegistro().toString());
        dto.setContrasenha(contrasenhaEncriptada);
        TUserProfile perfil = new TUserProfile();
        perfil.setIdPerfil(UUID.randomUUID().toString());
        perfil.setIdUsuario(usuario);

        TCareer carrera = dto.getIdCarrera() != null ? repoCareer.findById(dto.getIdCarrera()).orElse(null) : null;
        perfil.setIdCarrera(carrera);

        perfil.setNombre(dto.getNombre());
        perfil.setApellidos(dto.getApellidos());
        perfil.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

        String[] nombres = dto.getNombre().split(" ");
        String[] apellidos = dto.getApellidos().split(" ");
        String nombre = nombres.length > 0 ? nombres[0] : "";
        String apellido = apellidos.length > 0 ? apellidos[0] : "";
        String avatarUrl = "https://ui-avatars.com/api/?name="
                + URLEncoder.encode(nombre + " " + apellido, StandardCharsets.UTF_8) + "&background=random";

        byte[] imagenBytes = Validation.descargarImagen(avatarUrl);

        String nombreCarrera = (carrera != null) ? Validation.normalizarNombreCarrera(carrera.getNombre())
                : "sin_carrera";
        //subir foto perfil
        String perfilPath = nombreCarrera + "/perfil/" + usuario.getIdUsuario() + "_avatar.png";

        String perfilUrl = supabaseStorageService.uploadFileUrl(imagenBytes, perfilPath, "image/png");
        perfil.setFotoPerfil(perfilUrl);

        //subir foto portada
        String coverPath = nombreCarrera + "/portada/" + usuario.getIdUsuario() +"default_cover.webp";
        byte[] coverImageBytes = Files.readAllBytes(Paths.get("src/main/resources/static/images/default_cover.webp"));

        String coverUrl = supabaseStorageService.uploadFileUrl(coverImageBytes, coverPath, "image/webp");
        perfil.setFotoPortada(coverUrl);

        repoUserProfile.save(perfil);
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
    public boolean update(DtoUser dtoUser) throws Exception {
        Optional<TUser> tUsers = repoUser.findById(dtoUser.getIdUsuario());

        if (!tUsers.isPresent()) {
            return false;
        }

        TUser tUser = tUsers.get();
        tUser.setEmail(dtoUser.getEmail());
        String contrasenhaEncriptada = AesUtil.encrypt(dtoUser.getContrasenha());
        tUser.setContrasenha(contrasenhaEncriptada);
        tUser.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

        repoUser.save(tUser);

        dtoUser.setContrasenha(contrasenhaEncriptada);
        dtoUser.setFechaRegistro(tUser.getFechaRegistro());
        dtoUser.setFechaActualizacion(tUser.getFechaActualizacion());
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
