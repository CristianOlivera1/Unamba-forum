package foro.Unamba_forum.Business;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import foro.Unamba_forum.Dto.DtoUserProfile;
import foro.Unamba_forum.Entity.TCareer;
import foro.Unamba_forum.Entity.TUser;
import foro.Unamba_forum.Entity.TUserProfile;
import foro.Unamba_forum.Helper.Validation;
import foro.Unamba_forum.Repository.RepoCareer;
import foro.Unamba_forum.Repository.RepoUser;
import foro.Unamba_forum.Repository.RepoUserProfile;
import jakarta.transaction.Transactional;

@Service
public class BusinessUserProfile {
    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.bucket}")
    private String bucketName;

    @Autowired
    private RepoUserProfile repoUserProfile;

    @Autowired
    private RepoUser repoUser;

    @Autowired
    private RepoCareer repoCareer;

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    @Transactional
    public void insert(DtoUserProfile dtoUserProfile, MultipartFile fotoPerfil, MultipartFile fotoPortada) {
        dtoUserProfile.setIdPerfil(UUID.randomUUID().toString());
        dtoUserProfile.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

        TUserProfile tUserProfile = new TUserProfile();
        tUserProfile.setIdPerfil(dtoUserProfile.getIdPerfil());
        tUserProfile.setNombre(dtoUserProfile.getNombre());
        tUserProfile.setApellidos(dtoUserProfile.getApellidos());
        tUserProfile.setFechaNacimiento(dtoUserProfile.getFechaNacimiento());
        tUserProfile.setGenero(dtoUserProfile.getGenero());
        tUserProfile.setFechaActualizacion(dtoUserProfile.getFechaActualizacion());

        // Relacionar usuario y carrera con validación
        Optional<TUser> usuario = repoUser.findById(dtoUserProfile.getIdUsuario());
        if (usuario.isPresent()) {
            tUserProfile.setIdUsuario(usuario.get());
        } else {
            throw new RuntimeException("Usuario no encontrado");
        }

        String nombreCarrera = "sin_carrera"; // Si el usuario no tiene carrera

        if (dtoUserProfile.getIdCarrera() != null) {
            Optional<TCareer> carrera = repoCareer.findById(dtoUserProfile.getIdCarrera());
            if (carrera.isPresent()) {
                tUserProfile.setIdCarrera(carrera.get());
                nombreCarrera = Validation.normalizarNombreCarrera(carrera.get().getNombre());
            } else {
                throw new RuntimeException("Carrera no encontrada");
            }
        }

        // Subir foto de perfil solo si no está vacía
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            String perfilNombreLimpio = Validation.normalizarNombreArchivo(fotoPerfil.getOriginalFilename());
            String perfilPath = nombreCarrera + "/perfil/" + dtoUserProfile.getIdUsuario() + "_"
                    + perfilNombreLimpio.replaceAll("\\.(jpg|jpeg|png)$", ".webp");
            String perfilUrl = subirImagenTransformada(fotoPerfil, perfilPath);
            tUserProfile.setFotoPerfil(perfilUrl);
        }

        // Subir foto de portada solo si no está vacía
        if (fotoPortada != null && !fotoPortada.isEmpty()) {
            String portadaNombreLimpio = Validation.normalizarNombreArchivo(fotoPortada.getOriginalFilename());
            String portadaPath = nombreCarrera + "/portada/" + dtoUserProfile.getIdUsuario() + "_"
                    + portadaNombreLimpio.replaceAll("\\.(jpg|jpeg|png)$", ".webp");
            String portadaUrl = subirImagenTransformada(fotoPortada, portadaPath);
            tUserProfile.setFotoPortada(portadaUrl);
        }

        repoUserProfile.save(tUserProfile);
    }

    // Método para transformar la imagen a WebP y subirla
    private String subirImagenTransformada(MultipartFile file, String path) {
        try {
            // Leer la imagen desde MultipartFile
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            boolean success = ImageIO.write(originalImage, "webp", baos);
            if (!success) {
                throw new RuntimeException("Error al convertir la imagen a WebP");
            }
            byte[] webpBytes = baos.toByteArray();
            // Subir la imagen transformada
            return supabaseStorageService.uploadFile(webpBytes, path, "image/webp");
        } catch (IOException e) {
            throw new RuntimeException("Error al transformar imagen a WebP: " + e.getMessage());
        }
    }

    public List<DtoUserProfile> getAll() {
        List<TUserProfile> tUserProfiles = repoUserProfile.findAll();
        List<DtoUserProfile> dtoUserProfiles = new ArrayList<>();

        for (TUserProfile profile : tUserProfiles) {
            DtoUserProfile dtoUserProfile = new DtoUserProfile();
            dtoUserProfile.setIdPerfil(profile.getIdPerfil());
            dtoUserProfile.setNombre(profile.getNombre());
            dtoUserProfile.setApellidos(profile.getApellidos());
            dtoUserProfile.setFotoPerfil(profile.getFotoPerfil());
            dtoUserProfile.setFotoPortada(profile.getFotoPortada());
            dtoUserProfile.setFechaNacimiento(profile.getFechaNacimiento());
            dtoUserProfile.setGenero(profile.getGenero());
            dtoUserProfile.setFechaActualizacion(profile.getFechaActualizacion());
            // Asignar las relaciones
            dtoUserProfile.setIdUsuario(profile.getIdUsuario().getIdUsuario());
            if (profile.getIdCarrera() != null) {
                dtoUserProfile.setIdCarrera(profile.getIdCarrera().getIdCarrera());
            }
            dtoUserProfiles.add(dtoUserProfile);
        }
        return dtoUserProfiles;
    }

    //obtener un perfil por id de usuario
    public DtoUserProfile getByIdUsuario(String idUsuario) {
        Optional<TUser> usuario = repoUser.findById(idUsuario);
        if (usuario.isPresent()) {
            TUser user = usuario.get();
            Optional<TUserProfile> tUserProfile = repoUserProfile.findByIdUsuario(user);
            if (tUserProfile.isPresent()) {
                TUserProfile profile = tUserProfile.get();
                DtoUserProfile dtoUserProfile = new DtoUserProfile();
                dtoUserProfile.setIdPerfil(profile.getIdPerfil());
                dtoUserProfile.setNombre(profile.getNombre());
                dtoUserProfile.setApellidos(profile.getApellidos());
                dtoUserProfile.setFotoPerfil(profile.getFotoPerfil());
                dtoUserProfile.setFotoPortada(profile.getFotoPortada());
                dtoUserProfile.setFechaNacimiento(profile.getFechaNacimiento());
                dtoUserProfile.setGenero(profile.getGenero());
                dtoUserProfile.setFechaActualizacion(profile.getFechaActualizacion());
                dtoUserProfile.setIdUsuario(profile.getIdUsuario().getIdUsuario());
                if (profile.getIdCarrera() != null) {
                    dtoUserProfile.setIdCarrera(profile.getIdCarrera().getIdCarrera());
                }
                return dtoUserProfile;
            }
        }
        return null;
    }

    //obtener un perfil por id de perfil
    public DtoUserProfile getById(String idPerfil) {
        Optional<TUserProfile> tUserProfile = repoUserProfile.findById(idPerfil);
        if (tUserProfile.isPresent()) {
            TUserProfile profile = tUserProfile.get();
            DtoUserProfile dtoUserProfile = new DtoUserProfile();
            dtoUserProfile.setIdPerfil(profile.getIdPerfil());
            dtoUserProfile.setNombre(profile.getNombre());
            dtoUserProfile.setApellidos(profile.getApellidos());
            dtoUserProfile.setFotoPerfil(profile.getFotoPerfil());
            dtoUserProfile.setFotoPortada(profile.getFotoPortada());
            dtoUserProfile.setFechaNacimiento(profile.getFechaNacimiento());
            dtoUserProfile.setGenero(profile.getGenero());
            dtoUserProfile.setFechaActualizacion(profile.getFechaActualizacion());
            // Asignar las relaciones
            dtoUserProfile.setIdUsuario(profile.getIdUsuario().getIdUsuario());
            if (profile.getIdCarrera() != null) {
                dtoUserProfile.setIdCarrera(profile.getIdCarrera().getIdCarrera());
            }

            return dtoUserProfile;
        }
        return null;
    }

    @Transactional
    public void update(DtoUserProfile dtoUserProfile, MultipartFile fotoPerfil, MultipartFile fotoPortada) {
        Optional<TUserProfile> tUserProfile = repoUserProfile.findById(dtoUserProfile.getIdPerfil());
    
        if (tUserProfile.isPresent()) {
            TUserProfile profile = tUserProfile.get();
    
            profile.setNombre(dtoUserProfile.getNombre());
            profile.setApellidos(dtoUserProfile.getApellidos());
            profile.setFechaNacimiento(dtoUserProfile.getFechaNacimiento());
            profile.setGenero(dtoUserProfile.getGenero());
            profile.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
    
            // Obtener nombre de la carrera
            String nombreCarrera = "sin_carrera";
            if (dtoUserProfile.getIdCarrera() != null) {
                Optional<TCareer> carrera = repoCareer.findById(dtoUserProfile.getIdCarrera());
                if (carrera.isPresent()) {
                    profile.setIdCarrera(carrera.get());
                    nombreCarrera = Validation.normalizarNombreCarrera(carrera.get().getNombre());
                } else {
                    throw new RuntimeException("Carrera no encontrada");
                }
            } else {
                profile.setIdCarrera(null);
            }
    
            // Actualizar foto de perfil
            if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                if (profile.getFotoPerfil() != null) {
                    String oldPerfilPath = profile.getFotoPerfil().replace(supabaseUrl + "/storage/v1/object/public/", "");
                    boolean eliminado = supabaseStorageService.deleteFile(oldPerfilPath);
                    if (!eliminado) {
                        System.out.println("No se pudo eliminar la imagen anterior: " + oldPerfilPath);
                    }
                }
    
                // Normalizar nombre y convertir a WebP
                String perfilNombreLimpio = Validation.normalizarNombreArchivo(fotoPerfil.getOriginalFilename());
                String perfilPath = nombreCarrera + "/perfil/" + dtoUserProfile.getIdUsuario() + "_" + perfilNombreLimpio.replaceAll("\\.(jpg|jpeg|png)$", ".webp");
                String perfilUrl = subirImagenTransformada(fotoPerfil, perfilPath);
                profile.setFotoPerfil(perfilUrl);
            }
    
            // Actualizar foto de portada
            if (fotoPortada != null && !fotoPortada.isEmpty()) {
                if (profile.getFotoPortada() != null) {
                    String oldPortadaPath = profile.getFotoPortada().replace(supabaseUrl + "/storage/v1/object/public/", "");
                    boolean eliminado = supabaseStorageService.deleteFile(oldPortadaPath);
                    if (!eliminado) {
                        System.out.println("No se pudo eliminar la imagen anterior: " + oldPortadaPath);
                    }
                }
    
                // Normalizar nombre y convertir a WebP
                String portadaNombreLimpio = Validation.normalizarNombreArchivo(fotoPortada.getOriginalFilename());
                String portadaPath = nombreCarrera + "/portada/" + dtoUserProfile.getIdUsuario() + "_" + portadaNombreLimpio.replaceAll("\\.(jpg|jpeg|png)$", ".webp");
                String portadaUrl = subirImagenTransformada(fotoPortada, portadaPath);
                profile.setFotoPortada(portadaUrl);
            }
    
            // Actualizar usuario
            Optional<TUser> usuario = repoUser.findById(dtoUserProfile.getIdUsuario());
            usuario.ifPresentOrElse(profile::setIdUsuario, () -> {
                throw new RuntimeException("Usuario no encontrado");
            });
    
            repoUserProfile.save(profile);
        } else {
            throw new RuntimeException("Perfil no encontrado");
        }
    }

    @Transactional
    public void delete(String idPerfil) {
        repoUserProfile.deleteById(idPerfil);
    }
}
