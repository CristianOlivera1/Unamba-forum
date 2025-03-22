package foro.Unamba_forum.Business;

import java.sql.Timestamp;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import foro.Unamba_forum.Dto.DtoUserProfile;
import foro.Unamba_forum.Entity.TCareer;
import foro.Unamba_forum.Entity.TUser;
import foro.Unamba_forum.Entity.TUserProfile;
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
    dtoUserProfile.setFechaActualizacion(new Timestamp(new Date().getTime()));

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
            nombreCarrera = normalizarNombreCarrera(carrera.get().getNombre());
        } else {
            throw new RuntimeException("Carrera no encontrada");
        }
    }

    // Subir foto de perfil con la carrera correspondiente
    String perfilPath = nombreCarrera + "/perfil/" + UUID.randomUUID() + "_" + fotoPerfil.getOriginalFilename();
    String perfilUrl = subirImagenConMimeType(fotoPerfil, perfilPath);
    tUserProfile.setFotoPerfil(perfilUrl);

    // Subir foto de portada con la carrera correspondiente
    String portadaPath = nombreCarrera + "/portada/" + UUID.randomUUID() + "_" + fotoPortada.getOriginalFilename();
    String portadaUrl = subirImagenConMimeType(fotoPortada, portadaPath);
    tUserProfile.setFotoPortada(portadaUrl);

    // Guardar el perfil en la base de datos
    repoUserProfile.save(tUserProfile);
}

/**
 * Normaliza el nombre de la carrera para usarlo como nombre de carpeta en Supabase Storage.
 */
private String normalizarNombreCarrera(String nombre) {
    // Eliminar acentos y caracteres especiales
    String temp = Normalizer.normalize(nombre, Normalizer.Form.NFD);
    Pattern pattern = Pattern.compile("[^\\p{ASCII}]");
    temp = pattern.matcher(temp).replaceAll("");

    // Reemplazar espacios por guiones bajos y convertir a minúsculas
    return temp.replaceAll(" ", "_").toLowerCase();
}

 // Método para subir imágenes asegurando que tengan el tipo MIME correcto
private String subirImagenConMimeType(MultipartFile file, String path) {
    String contentType = file.getContentType(); // Obtener el tipo MIME original
    if (contentType == null || contentType.equals("application/octet-stream")) {
        // Definir un tipo MIME predeterminado si es necesario
        contentType = "image/png"; // O image/jpeg según el caso
    }

    return supabaseStorageService.uploadFile(file, path, contentType);
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
            profile.setFechaActualizacion(new Timestamp(new Date().getTime()));
    
            // Actualizar foto de perfil
            if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                if (profile.getFotoPerfil() != null) {
                    // Eliminar imagen anterior en Supabase
                    String oldPerfilPath = profile.getFotoPerfil().replace(supabaseUrl + "/storage/v1/object/public/" + bucketName + "/", "");
                    supabaseStorageService.deleteFile(oldPerfilPath);
                }
    
                String perfilPath = "perfil/" + UUID.randomUUID() + "_" + fotoPerfil.getOriginalFilename();
                String perfilUrl = supabaseStorageService.uploadFile(fotoPerfil, perfilPath, fotoPerfil.getContentType());
                profile.setFotoPerfil(perfilUrl);
            }
    
            // Actualizar foto de portada
            if (fotoPortada != null && !fotoPortada.isEmpty()) {
                if (profile.getFotoPortada() != null) {
                    // Eliminar imagen anterior en Supabase
                    String oldPortadaPath = profile.getFotoPortada().replace(supabaseUrl + "/storage/v1/object/public/" + bucketName + "/", "");
                    supabaseStorageService.deleteFile(oldPortadaPath);
                }
    
                String portadaPath = "portada/" + UUID.randomUUID() + "_" + fotoPortada.getOriginalFilename();
                String portadaUrl = supabaseStorageService.uploadFile(fotoPortada, portadaPath, fotoPortada.getContentType());
                profile.setFotoPortada(portadaUrl);
            }
    
            // Actualizar usuario
            Optional<TUser> usuario = repoUser.findById(dtoUserProfile.getIdUsuario());
            usuario.ifPresentOrElse(profile::setIdUsuario, () -> { throw new RuntimeException("Usuario no encontrado"); });
    
            // Actualizar carrera
            if (dtoUserProfile.getIdCarrera() != null) {
                Optional<TCareer> carrera = repoCareer.findById(dtoUserProfile.getIdCarrera());
                carrera.ifPresentOrElse(profile::setIdCarrera, () -> { throw new RuntimeException("Carrera no encontrada"); });
            } else {
                profile.setIdCarrera(null);
            }
    
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
