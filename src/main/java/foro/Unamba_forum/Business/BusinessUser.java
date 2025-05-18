package foro.Unamba_forum.Business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoRegisterUser;
import foro.Unamba_forum.Dto.DtoUser;
import foro.Unamba_forum.Dto.DtoUserProfile;
import foro.Unamba_forum.Entity.TCareer;
import foro.Unamba_forum.Entity.TNotification;
import foro.Unamba_forum.Entity.TRol;
import foro.Unamba_forum.Entity.TUser;
import foro.Unamba_forum.Entity.TUserProfile;
import foro.Unamba_forum.Helper.AesUtil;
import foro.Unamba_forum.Helper.JwtUtil;
import foro.Unamba_forum.Helper.Validation;
import foro.Unamba_forum.Repository.RepoCareer;
import foro.Unamba_forum.Repository.RepoFollowUp;
import foro.Unamba_forum.Repository.RepoRol;
import foro.Unamba_forum.Repository.RepoUser;
import foro.Unamba_forum.Repository.RepoUserProfile;
import jakarta.transaction.Transactional;

@Service
public class BusinessUser {
    @Value("${avatar.service.url}")
    private String avatarUrlService;

    @Autowired
    private RepoUser repoUser;

    @Autowired
    private RepoUserProfile repoUserProfile;

    @Autowired
    private RepoCareer repoCareer;

    @Autowired
    private RepoRol repoRol;

    @Autowired
    private RepoFollowUp repofollowUp;

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    @Autowired
    private BusinessNotification notificacionService;

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
        return repoUser.findById(idUsuario).isPresent();
    }

    public DtoUser login(String email, String contrasenha) throws Exception {
        Optional<TUser> tUserOptional = repoUser.findByEmail(email);

        if (!tUserOptional.isPresent()) {
            return null;
        }

        TUser tUser = tUserOptional.get();
        if (!AesUtil.decrypt(tUser.getContrasenha()).equals(contrasenha)) {
            return null;
        }

        DtoUser dtoUser = new DtoUser();
        dtoUser.setIdUsuario(tUser.getIdUsuario());
        dtoUser.setEmail(tUser.getEmail());
        dtoUser.setEmail(tUser.getEmail());
        dtoUser.setFechaActualizacion(tUser.getFechaActualizacion());
        dtoUser.setFechaRegistro(tUser.getFechaRegistro());
        dtoUser.setContrasenha(tUser.getContrasenha());
        dtoUser.setIdRol(tUser.getRol().getIdRol());
        dtoUser.setJwtToken(new JwtUtil().generateToken(dtoUser.getIdUsuario(), dtoUser.getEmail()));

        return dtoUser;
    }

    @Transactional
    public void registrarUsuarioConGoogle(DtoRegisterUser dto) throws Exception {

        TUser usuario = new TUser();
        usuario.setIdUsuario(UUID.randomUUID().toString());
        usuario.setEmail(dto.getEmail());
        usuario.setContrasenha(null);
        usuario.setFechaRegistro(new Timestamp(System.currentTimeMillis()));

        // Asignar rol según el dominio del correo
        TRol rol = dto.getEmail().endsWith("@unamba.edu.pe")
                ? repoRol.findByTipo(TRol.TipoRol.ESTUDIANTE)
                        .orElseThrow(() -> new Exception("Rol ESTUDIANTE no configurado"))
                : repoRol.findByTipo(TRol.TipoRol.INVITADO)
                        .orElseThrow(() -> new Exception("Rol INVITADO no configurado"));
        usuario.setRol(rol);

        repoUser.save(usuario);

        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setIdRol(usuario.getRol().getIdRol());
        dto.setFechaRegistro(usuario.getFechaRegistro());

        // Crear el perfil de usuario
        TUserProfile perfil = new TUserProfile();
        perfil.setIdPerfil(UUID.randomUUID().toString());
        perfil.setIdUsuario(usuario);
        perfil.setNombre(dto.getNombre());
        perfil.setApellidos(dto.getApellidos());
        perfil.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

        TCareer carrera = dto.getIdCarrera() != null ? repoCareer.findById(dto.getIdCarrera()).orElse(null) : null;
        perfil.setIdCarrera(carrera);

        String perfilUrl = subirFotoPerfil(dto, usuario, carrera);
        perfil.setFotoPerfil(perfilUrl);

        String coverUrl = subirFotoPortada(usuario, carrera);
        perfil.setFotoPortada(coverUrl);

        repoUserProfile.save(perfil);

        String idActor = "7213bed0-624b-4301-b6f7-aa5e4106f0c0";
        String mensaje = "⭐¡Bienvenido a la plataforma, " + dto.getNombre() + " " + dto.getApellidos() + "✨🎉!";
        notificacionService.createNotification(
                usuario.getIdUsuario(),
                idActor,
                mensaje,
                TNotification.TipoNotificacion.BIENVENIDA,
                null);
    }

    @Transactional
    public void registrarUsuario(DtoRegisterUser dto) throws Exception {
        TUser usuario = new TUser();
        usuario.setIdUsuario(UUID.randomUUID().toString());
        usuario.setEmail(dto.getEmail());
        String contrasenhaEncriptada = AesUtil.encrypt(dto.getContrasenha());
        usuario.setContrasenha(contrasenhaEncriptada);
        usuario.setFechaRegistro(new Timestamp(System.currentTimeMillis()));

        TRol rolInvitado = repoRol.findByTipo(TRol.TipoRol.INVITADO)
                .orElseThrow(() -> new Exception("Rol INVITADO no configurado"));
        usuario.setRol(rolInvitado);

        repoUser.save(usuario);
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setIdRol(usuario.getRol().getIdRol());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        dto.setContrasenha(contrasenhaEncriptada);
        dto.setJwtToken(new JwtUtil().generateToken(dto.getIdUsuario(), dto.getEmail()));
        // Crear el perfil de usuario
        TUserProfile perfil = new TUserProfile();
        perfil.setIdPerfil(UUID.randomUUID().toString());
        perfil.setIdUsuario(usuario);
        perfil.setNombre(Validation.capitalizeEachWord(dto.getNombre()));
        perfil.setApellidos(Validation.capitalizeEachWord(dto.getApellidos()));
        perfil.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        perfil.setGenero(dto.getGenero());

        TCareer carrera = dto.getIdCarrera() != null ? repoCareer.findById(dto.getIdCarrera()).orElse(null) : null;
        perfil.setIdCarrera(carrera);

        // Subir foto de perfil
        String perfilUrl = subirFotoPerfil(dto, usuario, carrera);
        perfil.setFotoPerfil(perfilUrl);

        // Subir foto de portada
        String coverUrl = subirFotoPortada(usuario, carrera);
        perfil.setFotoPortada(coverUrl);

        repoUserProfile.save(perfil);

        String idActor = "7213bed0-624b-4301-b6f7-aa5e4106f0c0";
        String mensaje = "⭐¡Bienvenido a la plataforma, " + dto.getNombre() + " " + dto.getApellidos() + "✨🎉!";
        notificacionService.createNotification(
                usuario.getIdUsuario(),
                idActor,
                mensaje,
                TNotification.TipoNotificacion.BIENVENIDA,
                null);

    }

    private String subirFotoPerfil(DtoRegisterUser dto, TUser usuario, TCareer carrera) throws Exception {
        String nombreCarrera = (carrera != null) ? Validation.normalizarNombreCarrera(carrera.getNombre())
                : "sin_carrera";
        String perfilPath = nombreCarrera + "/perfil/" + usuario.getIdUsuario() + "_avatar.png";

        byte[] imagenBytes;

        if (dto.getAvatar() != null && !dto.getAvatar().isEmpty()) {
            imagenBytes = Validation.descargarImagen(dto.getAvatar());
            if (imagenBytes == null || imagenBytes.length == 0) {
                throw new RuntimeException("La imagen descargada desde Google está vacía.");
            }
        } else {
            // Generar una imagen predeterminada si no hay avatar
            String[] nombres = dto.getNombre().split(" ");
            String[] apellidos = dto.getApellidos().split(" ");
            String nombre = nombres.length > 0 ? nombres[0] : "";
            String apellido = apellidos.length > 0 ? apellidos[0] : "";
            String avatarUrl = avatarUrlService + "?name="
                    + URLEncoder.encode(nombre + " " + apellido, StandardCharsets.UTF_8) + "&background=random";

            System.out.println("Generando imagen predeterminada desde URL: " + avatarUrl);
            imagenBytes = Validation.descargarImagen(avatarUrl);
            if (imagenBytes == null || imagenBytes.length == 0) {
                throw new RuntimeException("No se pudo generar la imagen predeterminada.");
            }
        }

        return supabaseStorageService.uploadFileUrl(imagenBytes, perfilPath, "image/png");
    }

    private String subirFotoPortada(TUser usuario, TCareer carrera) throws Exception {
        String nombreCarrera = (carrera != null) ? Validation.normalizarNombreCarrera(carrera.getNombre())
                : "sin_carrera";
        String coverPath = nombreCarrera + "/portada/" + usuario.getIdUsuario() + "default_cover.webp";
        byte[] coverImageBytes = Files.readAllBytes(Paths.get("src/main/resources/static/images/default_cover.webp"));

        return supabaseStorageService.uploadFileUrl(coverImageBytes, coverPath, "image/webp");
    }

    public DtoUser getUserById(String idUsuario) {
        Optional<TUser> tUser = repoUser.findById(idUsuario);

        if (!tUser.isPresent()) {
            return null;
        }

        TUser user = tUser.get();
        DtoUser dtoUser = new DtoUser();
        dtoUser.setIdUsuario(user.getIdUsuario());
        dtoUser.setIdRol(user.getRol().getIdRol());
        dtoUser.setEmail(user.getEmail());
        dtoUser.setContrasenha(user.getContrasenha());
        dtoUser.setFechaRegistro(user.getFechaRegistro());
        dtoUser.setFechaActualizacion(user.getFechaActualizacion());

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

        if (dtoUser.getContrasenha() != null && !dtoUser.getContrasenha().isEmpty()) {
            String contrasenhaEncriptada = AesUtil.encrypt(dtoUser.getContrasenha());
            tUser.setContrasenha(contrasenhaEncriptada);
            dtoUser.setContrasenha(contrasenhaEncriptada);
        } else {
            dtoUser.setContrasenha(tUser.getContrasenha());
        }
        tUser.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

        repoUser.save(tUser);

        dtoUser.setFechaRegistro(tUser.getFechaRegistro());
        dtoUser.setFechaActualizacion(tUser.getFechaActualizacion());
        return true;
    }

    // Obtener 5 usuarios aleatorios
    public List<DtoUserProfile> getRandomUsers(int count) {
        List<TUserProfile> profiles = repoUserProfile.findRandomUsers(count);
        return profiles.stream().map(this::convertToDtoUserProfile).collect(Collectors.toList());
    }

    public List<DtoUserProfile> getSuggestedUsers(String idUsuario, int count) {
        TUser currentUser = repoUser.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener los usuarios que el usuario actual está siguiendo
        List<TUser> followingUsers = repofollowUp.findBySeguidor(currentUser)
                .stream()
                .map(followUp -> followUp.getSeguido())
                .collect(Collectors.toList());

        // Obtener los usuarios que siguen a las personas que el usuario actual sigue
        List<TUser> followersOfFollowing = followingUsers.stream()
                .flatMap(user -> repofollowUp.findBySeguido(user).stream())
                .map(followUp -> followUp.getSeguidor())
                .distinct()
                .collect(Collectors.toList());

        // Obtener los usuarios de la misma carrera
        TUserProfile currentUserProfile = repoUserProfile.findByIdUsuario(currentUser)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));
        List<TUserProfile> sameCareerUsers = repoUserProfile.findByIdCarrera(
                currentUserProfile.getIdCarrera() != null ? currentUserProfile.getIdCarrera().getIdCarrera() : null);

        // Combinar todas las listas y eliminar duplicados
        List<TUserProfile> combinedUsers = new ArrayList<>();
        combinedUsers.addAll(followingUsers.stream()
                .map(user -> repoUserProfile.findByIdUsuario(user).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        combinedUsers.addAll(followersOfFollowing.stream()
                .map(user -> repoUserProfile.findByIdUsuario(user).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        combinedUsers.addAll(sameCareerUsers);
        combinedUsers = combinedUsers.stream().distinct().collect(Collectors.toList());

        combinedUsers = combinedUsers.stream()
                .filter(profile -> !profile.getIdUsuario().getIdUsuario().equals(idUsuario))
                .collect(Collectors.toList());

        // Si no hay suficientes usuarios sugeridos, completar con usuarios aleatorios
        if (combinedUsers.size() < count) {
            List<TUserProfile> randomUsers = repoUserProfile.findRandomUsers(count - combinedUsers.size());
            combinedUsers.addAll(randomUsers);
            combinedUsers = combinedUsers.stream().distinct().collect(Collectors.toList());
        }

        // Seleccionar 5 usuarios aleatorios
        Collections.shuffle(combinedUsers);
        List<TUserProfile> suggestedUsers = combinedUsers.stream()
                .limit(count)
                .collect(Collectors.toList());

        // Convertir a DTO
        return suggestedUsers.stream()
                .map(this::convertToDtoUserProfile)
                .collect(Collectors.toList());
    }

    private DtoUserProfile convertToDtoUserProfile(TUserProfile profile) {
        DtoUserProfile dto = new DtoUserProfile();
        dto.setIdPerfil(profile.getIdPerfil());
        dto.setIdUsuario(profile.getIdUsuario().getIdUsuario());
        dto.setIdCarrera(profile.getIdCarrera() != null ? profile.getIdCarrera().getIdCarrera() : null);
        dto.setNombreCarrera(profile.getIdCarrera() != null ? profile.getIdCarrera().getNombre() : "Sin carrera");
        dto.setNombre(profile.getNombre());
        dto.setApellidos(profile.getApellidos());
        dto.setDescripcion(profile.getDescripcion());
        dto.setFotoPerfil(profile.getFotoPerfil());
        dto.setFotoPortada(profile.getFotoPortada());
        dto.setFechaNacimiento(profile.getFechaNacimiento());
        dto.setGenero(profile.getGenero());
        dto.setFechaActualizacion(profile.getFechaActualizacion());
        return dto;
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
