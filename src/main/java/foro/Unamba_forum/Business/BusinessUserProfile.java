package foro.Unamba_forum.Business;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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
        tUserProfile.setDescripcion(dtoUserProfile.getDescripcion());
        tUserProfile.setFechaNacimiento(dtoUserProfile.getFechaNacimiento());
        tUserProfile.setGenero(dtoUserProfile.getGenero());
        tUserProfile.setFechaActualizacion(dtoUserProfile.getFechaActualizacion());

        // Relacionar usuario y carrera con validación
        TUser usuario = repoUser.findById(dtoUserProfile.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        tUserProfile.setIdUsuario(usuario);

        String nombreCarrera = "sin_carrera"; // Si el usuario no tiene carrera

        if (dtoUserProfile.getIdCarrera() != null) {
            TCareer carrera = repoCareer.findById(dtoUserProfile.getIdCarrera())
                    .orElseThrow(() -> new RuntimeException("Carrera no encontrada"));
            tUserProfile.setIdCarrera(carrera);
            nombreCarrera = Validation.normalizarNombreCarrera(carrera.getNombre());
        }

        // Subir foto de perfil solo si no está vacía
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            String perfilPath = construirRutaImagen(nombreCarrera, dtoUserProfile.getIdUsuario(), fotoPerfil, "perfil");
            String perfilUrl = subirImagenTransformada(fotoPerfil, perfilPath);
            tUserProfile.setFotoPerfil(perfilUrl);
        }

        // Subir foto de portada solo si no está vacía
        if (fotoPortada != null && !fotoPortada.isEmpty()) {
            String portadaPath = construirRutaImagen(nombreCarrera, dtoUserProfile.getIdUsuario(), fotoPortada, "portada");
            String portadaUrl = subirImagenTransformada(fotoPortada, portadaPath);
            tUserProfile.setFotoPortada(portadaUrl);
        }

        repoUserProfile.save(tUserProfile);
    }

    private String construirRutaImagen(String nombreCarrera, String idUsuario, MultipartFile file, String tipo) {
        String nombreLimpio = Validation.normalizarNombreArchivo(file.getOriginalFilename());
        return nombreCarrera + "/" + tipo + "/" + idUsuario + "_" + nombreLimpio.replaceAll("\\.(jpg|jpeg|png)$", ".webp");
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
            DtoUserProfile dtoUserProfile = convertirAUserProfileDto(profile);
            dtoUserProfiles.add(dtoUserProfile);
        }
        return dtoUserProfiles;
    }

    public DtoUserProfile getByIdUsuario(String idUsuario) {
        TUser user = repoUser.findById(idUsuario).orElse(null);
        if (user != null) {
            TUserProfile profile = repoUserProfile.findByIdUsuario(user).orElse(null);
            if (profile != null) {
                return convertirAUserProfileDto(profile);
            }
        }
        return null;
    }

    public DtoUserProfile getById(String idPerfil) {
        TUserProfile profile = repoUserProfile.findById(idPerfil).orElse(null);
        if (profile != null) {
            return convertirAUserProfileDto(profile);
        }
        return null;
    }

    @Transactional
public void update(DtoUserProfile dtoUserProfile, MultipartFile fotoPerfil, MultipartFile fotoPortada) {
    TUserProfile profile = repoUserProfile.findById(dtoUserProfile.getIdPerfil())
            .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));

    // Actualizar solo los campos proporcionados (no null)
    if (dtoUserProfile.getNombre() != null) {
        profile.setNombre(dtoUserProfile.getNombre());
    }
    if (dtoUserProfile.getApellidos() != null) {
        profile.setApellidos(dtoUserProfile.getApellidos());
    }
    if (dtoUserProfile.getDescripcion() != null) {
        profile.setDescripcion(dtoUserProfile.getDescripcion());
    }
    if (dtoUserProfile.getFechaNacimiento() != null) {
        profile.setFechaNacimiento(dtoUserProfile.getFechaNacimiento());
    }
    if (dtoUserProfile.getGenero() != null) {
        profile.setGenero(dtoUserProfile.getGenero());
    }

    profile.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

    // Lógica para carrera
    if (dtoUserProfile.getIdCarrera() != null) {
        TCareer carrera = repoCareer.findById(dtoUserProfile.getIdCarrera())
                .orElseThrow(() -> new RuntimeException("Carrera no encontrada"));
        profile.setIdCarrera(carrera);
    }

    // Actualizar foto de perfil
    if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
        eliminarImagenAnterior(profile.getFotoPerfil());
        String perfilPath = construirRutaImagen("ruta", dtoUserProfile.getIdUsuario(), fotoPerfil, "perfil");
        String perfilUrl = subirImagenTransformada(fotoPerfil, perfilPath);
        profile.setFotoPerfil(perfilUrl);
    }

    // Actualizar foto de portada
    if (fotoPortada != null && !fotoPortada.isEmpty()) {
        eliminarImagenAnterior(profile.getFotoPortada());
        String portadaPath = construirRutaImagen("ruta", dtoUserProfile.getIdUsuario(), fotoPortada, "portada");
        String portadaUrl = subirImagenTransformada(fotoPortada, portadaPath);
        profile.setFotoPortada(portadaUrl);
    }

    // Actualizar el usuario si es necesario
    if (dtoUserProfile.getIdUsuario() != null) {
        TUser usuario = repoUser.findById(dtoUserProfile.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        profile.setIdUsuario(usuario);
    }

    // Guardar los cambios en el repositorio
    repoUserProfile.save(profile);
}


    private void eliminarImagenAnterior(String imageUrl) {
        if (imageUrl != null) {
            String oldPath = imageUrl.replace(supabaseUrl + "/storage/v1/object/public/", "");
            boolean eliminado = supabaseStorageService.deleteFile(oldPath);
            if (!eliminado) {
                System.out.println("No se pudo eliminar la imagen anterior: " + oldPath);
            }
        }
    }

    @Transactional
    public void delete(String idPerfil) {
        repoUserProfile.deleteById(idPerfil);
    }

    private DtoUserProfile convertirAUserProfileDto(TUserProfile profile) {
        DtoUserProfile dtoUserProfile = new DtoUserProfile();
        dtoUserProfile.setIdPerfil(profile.getIdPerfil());
        dtoUserProfile.setNombre(profile.getNombre());
        dtoUserProfile.setApellidos(profile.getApellidos());
        dtoUserProfile.setDescripcion(profile.getDescripcion());
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
