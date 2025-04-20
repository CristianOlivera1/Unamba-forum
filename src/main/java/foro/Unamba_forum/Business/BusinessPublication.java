package foro.Unamba_forum.Business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import foro.Unamba_forum.Dto.DtoFile;
import foro.Unamba_forum.Dto.DtoFixPublication;
import foro.Unamba_forum.Dto.DtoPublication;
import foro.Unamba_forum.Entity.TFile;
import foro.Unamba_forum.Entity.TFollowUp;
import foro.Unamba_forum.Entity.TNotification;
import foro.Unamba_forum.Entity.TPublication;
import foro.Unamba_forum.Entity.TUser;
import foro.Unamba_forum.Entity.TUserProfile;
import foro.Unamba_forum.Helper.Validation;
import foro.Unamba_forum.Repository.RepoCareer;
import foro.Unamba_forum.Repository.RepoCategory;
import foro.Unamba_forum.Repository.RepoFile;
import foro.Unamba_forum.Repository.RepoFollowUp;
import foro.Unamba_forum.Repository.RepoPublication;
import foro.Unamba_forum.Repository.RepoUser;
import foro.Unamba_forum.Repository.RepoUserProfile;
import jakarta.transaction.Transactional;

@Service
public class BusinessPublication {
    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.bucket2}")
    private String bucketName2;

    @Autowired
    private RepoPublication repoPublication;

    @Autowired
    private RepoFile repoArchivo;

    @Autowired
    private RepoCategory repoCategory;

    @Autowired
    private RepoUser repoUser;

    @Autowired
    private RepoFollowUp repoFollowUp;

    @Autowired
    private BusinessNotification notificacionService;

    @Autowired
    private RepoUserProfile repoUserProfile;

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    @Transactional
    public void insertPublication(DtoPublication dtoPublication) {
        TUser usuario = repoUser.findById(dtoPublication.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TUserProfile perfil = repoUserProfile.findByIdUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Perfil de usuario no encontrado"));

        if (perfil.getIdCarrera() == null) {
            throw new RuntimeException("El usuario no tiene una carrera asignada.");
        }

        TPublication publication = new TPublication();
        publication.setIdPublicacion(UUID.randomUUID().toString());
        publication.setUsuario(usuario);
        publication.setCarrera(perfil.getIdCarrera()); // Asignar la carrera del perfil del usuario
        publication.setCategoria(repoCategory.findById(dtoPublication.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada")));
        publication.setTitulo(Validation.capitalizeFirstLetter(dtoPublication.getTitulo()));
        publication.setContenido(Validation.capitalizeFirstLetter(dtoPublication.getContenido()));
        publication.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        publication.setFechaRegistro(new Timestamp(System.currentTimeMillis()));

        repoPublication.save(publication);
        dtoPublication.setIdPublicacion(publication.getIdPublicacion());
        dtoPublication.setFechaActualizacion(publication.getFechaActualizacion());
        dtoPublication.setFechaRegistro(publication.getFechaRegistro());

        if (dtoPublication.getArchivos() != null) {
            String nombreCarrera = Validation.normalizarNombreCarrera(publication.getCarrera().getNombre());
            String nombreCategoria = Validation.normalizarNombreArchivo(publication.getCategoria().getNombre());

            for (DtoFile dtoArchivo : dtoPublication.getArchivos()) {
                MultipartFile file = dtoArchivo.getFile();
                if (file == null) {
                    throw new RuntimeException("El archivo no puede ser nulo");
                }

                String rutaArchivo;
                if (file.getContentType().startsWith("image")) {
                    String path;
                    if (file.getOriginalFilename().endsWith(".webp")) {
                        // Manejar imágenes WebP directamente
                        path = construirRutaArchivo(nombreCarrera, nombreCategoria, publication.getIdPublicacion(),
                                file.getOriginalFilename());
                    } else {
                        // Transformar otros formatos a WebP
                        path = construirRutaArchivo(nombreCarrera, nombreCategoria, publication.getIdPublicacion(),
                                file.getOriginalFilename().replaceAll("\\.(jpg|jpeg|png)$", ".webp"));
                    }
                    rutaArchivo = subirImagenTransformada(file, path);
                } else if (file.getContentType().startsWith("video")) {
                    // Construir ruta y subir video sin transformación
                    String path = construirRutaArchivo(nombreCarrera, nombreCategoria, publication.getIdPublicacion(),
                            file.getOriginalFilename());
                    rutaArchivo = supabaseStorageService.uploadFile(file, path, bucketName2);
                } else {
                    throw new RuntimeException("Tipo de archivo no soportado: " + file.getContentType());
                }

                // Guardar archivo en la base de datos
                TFile archivo = new TFile();
                archivo.setIdArchivo(UUID.randomUUID().toString());
                archivo.setPublicacion(publication);
                archivo.setTipo(file.getContentType().startsWith("image") ? "imagen" : "video");
                archivo.setRutaArchivo(rutaArchivo);
                archivo.setFechaRegistro(new Timestamp(System.currentTimeMillis()));
                repoArchivo.save(archivo);

                dtoArchivo.setIdArchivo(archivo.getIdArchivo());
                dtoArchivo.setIdPublicacion(publication.getIdPublicacion());
                dtoArchivo.setRutaArchivo(rutaArchivo);
                dtoArchivo.setFechaRegistro(archivo.getFechaRegistro());
            }
        }
        // Notificar a los seguidores del usuario
        List<TFollowUp> seguidores = repoFollowUp.findBySeguido(publication.getUsuario());
        for (TFollowUp seguidor : seguidores) {
            notificacionService.createNotification(
                    seguidor.getSeguidor().getIdUsuario(), // Usuario que recibirá la notificación
                    publication.getUsuario().getIdUsuario(), // Usuario que realizó la publicación
                    "ha realizado una nueva publicación 📃: " + publication.getTitulo(),
                    TNotification.TipoNotificacion.PUBLICACION,
                    publication.getIdPublicacion());
        }

        // Notificar a todos los usuarios de la carrera
        List<TUser> usuariosCarrera = repoUser.findByCarrera(publication.getCarrera().getIdCarrera());
        for (TUser user : usuariosCarrera) {
            if (!user.getIdUsuario().equals(publication.getUsuario().getIdUsuario())) {
                notificacionService.createNotification(
                        user.getIdUsuario(),
                        publication.getUsuario().getIdUsuario(),
                        "ha realizado una nueva publicación 📃 en tu carrera: " + publication.getTitulo(),
                        TNotification.TipoNotificacion.PUBLICACION,
                        publication.getIdPublicacion());
            }
        }

    }

    private String construirRutaArchivo(String nombreCarrera, String nombreCategoria, String idPublicacion,
            String nombreArchivo) {
        String nombreLimpio = Validation.normalizarNombreArchivo(nombreArchivo);
        return nombreCarrera + "/" + nombreCategoria + "/" + idPublicacion + "/" + nombreLimpio;
    }

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
            return supabaseStorageService.uploadFile(webpBytes, path, "image/webp", bucketName2);
        } catch (IOException e) {
            throw new RuntimeException("Error al transformar imagen a WebP: " + e.getMessage());
        }
    }

    // Detalles
    public DtoPublication getPublicationDetails(String idPublicacion) {
        TPublication publication = repoPublication.findById(idPublicacion)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));
        return convertToDtoPublication(publication);
    }

    // Obtener publicaciones relacionadas
    public Page<DtoPublication> getRelatedPublications(String idCarrera, String idCategoria,
            String excludeIdPublicacion, Pageable pageable) {
        Page<TPublication> relatedPublications = repoPublication.findRelatedPublications(idCarrera, idCategoria,
                excludeIdPublicacion, pageable);
        return relatedPublications.map(this::convertToDtoPublication);
    }

    @Transactional
    public void updatePublication(DtoPublication dtoPublication) {
        TPublication publication = repoPublication.findById(dtoPublication.getIdPublicacion())
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        // Actualizar los datos de la publicación
        publication.setTitulo(dtoPublication.getTitulo());
        publication.setCategoria(repoCategory.findById(dtoPublication.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada")));
        publication.setContenido(dtoPublication.getContenido());
        publication.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

        repoPublication.save(publication);

        dtoPublication.setIdUsuario(publication.getUsuario().getIdUsuario());
        dtoPublication.setIdCarrera(publication.getCarrera().getIdCarrera());
        dtoPublication.setFechaRegistro(publication.getFechaRegistro());
        dtoPublication.setFechaActualizacion(publication.getFechaActualizacion());

        // Eliminar archivos existentes en Supabase y en la base de datos (si existen)
        List<TFile> existingFiles = repoArchivo.findByPublicacion(publication);
        if (!existingFiles.isEmpty()) {
            for (TFile archivo : existingFiles) {
                eliminarArchivoAnterior(archivo.getRutaArchivo());
            }
            repoArchivo.deleteAll(existingFiles);
        }

        // Guardar nuevos archivos si se proporcionan
        if (dtoPublication.getArchivos() != null && !dtoPublication.getArchivos().isEmpty()) {
            String nombreCarrera = Validation.normalizarNombreCarrera(publication.getCarrera().getNombre());
            String nombreCategoria = Validation.normalizarNombreArchivo(publication.getCategoria().getNombre());

            for (DtoFile dtoArchivo : dtoPublication.getArchivos()) {
                MultipartFile file = dtoArchivo.getFile();
                if (file == null) {
                    throw new RuntimeException("El archivo no puede ser nulo");
                }

                String rutaArchivo;
                if (file.getContentType().startsWith("image")) {
                    String path;
                    if (file.getOriginalFilename().endsWith(".webp")) {
                        // Manejar imágenes WebP directamente
                        path = construirRutaArchivo(nombreCarrera, nombreCategoria, publication.getIdPublicacion(),
                                file.getOriginalFilename());
                    } else {
                        // Transformar otros formatos a WebP
                        path = construirRutaArchivo(nombreCarrera, nombreCategoria, publication.getIdPublicacion(),
                                file.getOriginalFilename().replaceAll("\\.(jpg|jpeg|png)$", ".webp"));
                    }
                    rutaArchivo = subirImagenTransformada(file, path);
                } else if (file.getContentType().startsWith("video")) {
                    // Construir ruta y subir video sin transformación
                    String path = construirRutaArchivo(nombreCarrera, nombreCategoria, publication.getIdPublicacion(),
                            file.getOriginalFilename());
                    rutaArchivo = supabaseStorageService.uploadFile(file, path, bucketName2);
                } else {
                    throw new RuntimeException("Tipo de archivo no soportado: " + file.getContentType());
                }

                // Guardar archivo en la base de datos
                TFile archivo = new TFile();
                archivo.setIdArchivo(UUID.randomUUID().toString());
                archivo.setPublicacion(publication);
                archivo.setTipo(file.getContentType().startsWith("image") ? "imagen" : "video");
                archivo.setRutaArchivo(rutaArchivo);
                archivo.setFechaRegistro(new Timestamp(System.currentTimeMillis()));
                repoArchivo.save(archivo);

                dtoArchivo.setIdArchivo(archivo.getIdArchivo());
                dtoArchivo.setIdPublicacion(publication.getIdPublicacion());
                dtoArchivo.setRutaArchivo(rutaArchivo);
                dtoArchivo.setFechaRegistro(archivo.getFechaRegistro());
            }
        }
    }

    private void eliminarArchivoAnterior(String fileUrl) {
        if (fileUrl != null) {
            String oldPath = fileUrl.replace(supabaseUrl + "/storage/v1/object/public/", "");
            boolean eliminado = supabaseStorageService.deleteFile(oldPath);
            if (!eliminado) {
                System.out.println("No se pudo eliminar el archivo anterior: " + oldPath);
            }
        }
    }

    /* Fijar o desfijar publicacion */
    @Transactional
    public void fixPublication(DtoFixPublication dtoFixPublication) {
        TPublication publication = repoPublication.findById(dtoFixPublication.getIdPublicacion())
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        publication.setFijada(dtoFixPublication.isFijada());
        publication.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

        repoPublication.save(publication);
    }

    @Transactional
    public void deletePublication(String idPublicacion) {
        TPublication publication = repoPublication.findById(idPublicacion)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        List<TFile> archivos = repoArchivo.findByPublicacion(publication);
        repoArchivo.deleteAll(archivos);

        repoPublication.delete(publication);
    }

    private DtoPublication convertToDtoPublication(TPublication publication) {
        DtoPublication dto = new DtoPublication();
        dto.setIdPublicacion(publication.getIdPublicacion());
        dto.setIdUsuario(publication.getUsuario().getIdUsuario());
        dto.setIdCategoria(publication.getCategoria().getIdCategoria());
        dto.setIdCarrera(publication.getCarrera().getIdCarrera());
        dto.setTitulo(publication.getTitulo());
        dto.setContenido(publication.getContenido());
        dto.setFechaRegistro(publication.getFechaRegistro());
        dto.setFechaActualizacion(publication.getFechaActualizacion());

        // Obtener el perfil del usuario
        TUserProfile userProfile = repoUserProfile.findByIdUsuario(publication.getUsuario())
                .orElseThrow(() -> new RuntimeException("Perfil de usuario no encontrado"));

        // Establecer datos adicionales
        dto.setNombreCompleto(userProfile.getNombre() + " " + userProfile.getApellidos());
        dto.setAvatar(userProfile.getFotoPerfil());
        dto.setNombreCarrera(publication.getCarrera().getNombre());
        dto.setNombreCategoria(publication.getCategoria().getNombre());

        List<TFile> archivos = repoArchivo.findByPublicacion(publication);
        List<DtoFile> dtoArchivos = archivos.stream().map(this::convertToDtoArchivo).collect(Collectors.toList());
        dto.setArchivos(dtoArchivos);

        return dto;
    }

    public long getTotalPublications() {
        return repoPublication.count();
    }

    private DtoFile convertToDtoArchivo(TFile archivo) {
        DtoFile dto = new DtoFile();
        dto.setIdArchivo(archivo.getIdArchivo());
        dto.setIdPublicacion(archivo.getPublicacion().getIdPublicacion());
        dto.setTipo(archivo.getTipo());
        dto.setRutaArchivo(archivo.getRutaArchivo());
        dto.setFechaRegistro(archivo.getFechaRegistro());
        return dto;
    }

    // Obtener publicaciones con archivos paginadas
    public Page<DtoPublication> getPublicationsWithFilesPageable(Pageable pageable) {
        Page<TPublication> publications = repoPublication.findPublicationsWithFiles(pageable);
        return publications.map(this::convertToDtoPublication);
    }

    // Obtener publicaciones con archivos paginadas segun una carrera
    public Page<DtoPublication> getPublicationsWithFilesByCareerPageable(String idCarrera, Pageable pageable) {
        Page<TPublication> publications = repoPublication.findPublicationsWithFilesByCareer(idCarrera, pageable);
        return publications.map(this::convertToDtoPublication);
    }

    // Obtener publicaciones sin archivos paginadas
    public Page<DtoPublication> getPublicationsWithoutFilesPageable(Pageable pageable) {
        Page<TPublication> publications = repoPublication.findPublicationsWithoutFiles(pageable);
        return publications.map(this::convertToDtoPublication);
    }

    // Obtener publicaciones más recientes de un usuario paginadas
    public Page<DtoPublication> getRecentPublicationsByUser(String idUsuario, Pageable pageable) {
        Page<TPublication> publications = repoPublication.findByUsuarioIdOrderByFechaRegistroDesc(idUsuario, pageable);
        return publications.map(this::convertToDtoPublication);
    }

    // Obtener publicaciones sin archivos paginadas segun una carrera
    public Page<DtoPublication> getPublicationsWithoutFilesByCareerPageable(String idCarrera, Pageable pageable) {
        Page<TPublication> publications = repoPublication.findPublicationsWithoutFilesByCareer(idCarrera, pageable);
        return publications.map(this::convertToDtoPublication);
    }

}
