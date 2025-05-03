package foro.Unamba_forum.Service.Publication;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;

import foro.Unamba_forum.Business.BusinessPublication;
import foro.Unamba_forum.Dto.DtoFile;
import foro.Unamba_forum.Dto.DtoFixPublication;
import foro.Unamba_forum.Dto.DtoPublication;
import foro.Unamba_forum.Dto.DtoPublicationRelated;
import foro.Unamba_forum.Service.Generic.ResponseGeneric;
import foro.Unamba_forum.Service.Publication.RequestsObject.RequestInsertPublication;
import foro.Unamba_forum.Service.Publication.RequestsObject.RequestUpdatePublication;
import foro.Unamba_forum.Service.Publication.ResponseObject.ResponseGetAllPublication;
import foro.Unamba_forum.Service.Publication.ResponseObject.ResponseGetAllPublicationRelated;
import foro.Unamba_forum.Service.Publication.ResponseObject.ResponseInsertPublication;
import foro.Unamba_forum.Service.Publication.ResponseObject.ResponseUpdatePublication;

@RestController

@RequestMapping("/publication")
public class PublicationController {

    @Autowired
    private BusinessPublication businessPublication;

    @PostMapping("/insert")
    public ResponseEntity<ResponseInsertPublication> insertPublication(
            @ModelAttribute RequestInsertPublication request) {
        ResponseInsertPublication response = new ResponseInsertPublication();
        try {
            DtoPublication dtoPublication = new DtoPublication();
            dtoPublication.setIdUsuario(request.getIdUsuario());
            dtoPublication.setIdCategoria(request.getIdCategoria());
            dtoPublication.setTitulo(request.getTitulo());
            dtoPublication.setContenido(request.getContenido());

            // Procesar archivos y URLs
            List<DtoFile> archivos = new ArrayList<>();
            if (request.getArchivos() != null && !request.getArchivos().isEmpty()) {
                for (Object archivo : request.getArchivos()) {
                    DtoFile dtoArchivo = new DtoFile();
                    if (archivo instanceof MultipartFile) {
                        MultipartFile file = (MultipartFile) archivo;
                        dtoArchivo.setFile(file);

                        // Clasificar archivos según su tipo MIME o extensión
                        if (file.getContentType().equals("image/gif") || file.getOriginalFilename().endsWith(".gif")) {
                            dtoArchivo.setTipo("gif");
                        } else if (file.getContentType().startsWith("image")) {
                            dtoArchivo.setTipo("imagen");
                        } else if (file.getContentType().startsWith("video")) {
                            dtoArchivo.setTipo("video");
                        } else {
                            throw new RuntimeException("Tipo de archivo no soportado: " + file.getContentType());
                        }

                        dtoArchivo.setRutaArchivo(file.getOriginalFilename());
                    } else if (archivo instanceof String) {
                        String url = (String) archivo;
                        dtoArchivo.setRutaArchivo(url);

                        // Clasificar URLs basadas en la extensión
                        if (url.endsWith(".gif")) {
                            dtoArchivo.setTipo("gif");
                        } else if (url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".jpeg")) {
                            dtoArchivo.setTipo("imagen");
                        } else {
                            dtoArchivo.setTipo("video");
                        }
                    }
                    archivos.add(dtoArchivo);
                }
            }
            dtoPublication.setArchivos(archivos);

            businessPublication.insertPublication(dtoPublication);

            response.setType("success");
            response.setListMessage(List.of("Publicación insertada correctamente"));
            response.setData(dtoPublication);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al insertar la publicación: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /* Obtener las publicaciones de un usuario paginadas */
    @GetMapping("/user/{idUsuario}")
    public ResponseEntity<ResponseGetAllPublication> getRecentPublicationsByUser(
            @PathVariable String idUsuario,
            @RequestParam(defaultValue = "0") int page) {
        ResponseGetAllPublication response = new ResponseGetAllPublication();
        try {
            Page<DtoPublication> publications = businessPublication.getRecentPublicationsByUser(
                    idUsuario, PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "fechaRegistro")));
            response.setType("success");
            response.setData(publications.getContent());
            response.setListMessage(List.of("Publicaciones recientes del usuario obtenidas correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener publicaciones recientes del usuario: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // para obtener una publicacion en detalles y precargar para actualizar
    @GetMapping("/details/{idPublicacion}")
    public ResponseEntity<ResponseGeneric<DtoPublication>> getPublicationDetails(@PathVariable String idPublicacion) {
        ResponseGeneric<DtoPublication> response = new ResponseGeneric<>();
        try {
            DtoPublication publicationDetails = businessPublication.getPublicationDetails(idPublicacion);
            response.setType("success");
            response.setData(publicationDetails);
            response.setListMessage(List.of("Detalles de la publicación obtenidos correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener los detalles de la publicación: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // obtener publicacion relacionadas
    @GetMapping("/related/{idPublicacion}")
    public ResponseEntity<ResponseGetAllPublicationRelated> getRelatedPublications(
            @PathVariable String idPublicacion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        ResponseGetAllPublicationRelated response = new ResponseGetAllPublicationRelated();
        try {
            // Llamar al método de negocio para obtener publicaciones relacionadas
            Page<DtoPublicationRelated> relatedPublications = businessPublication.getRelatedPublications(
                    idPublicacion, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaRegistro")));

            response.setType("success");
            response.setData(relatedPublications.getContent());
            response.setListMessage(List.of("Publicaciones relacionadas obtenidas correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener publicaciones relacionadas: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseUpdatePublication> updatePublication(
            @ModelAttribute RequestUpdatePublication request) {
        ResponseUpdatePublication response = new ResponseUpdatePublication();
        try {
            // Crear el DTO de la publicación
            DtoPublication dtoPublication = new DtoPublication();
            dtoPublication.setIdPublicacion(request.getIdPublicacion());
            dtoPublication.setIdCategoria(request.getIdCategoria());
            dtoPublication.setTitulo(request.getTitulo());
            dtoPublication.setContenido(request.getContenido());

            // Procesar archivos y URLs
            List<DtoFile> archivos = new ArrayList<>();
            if (request.getArchivos() != null && !request.getArchivos().isEmpty()) {
                for (Object archivo : request.getArchivos()) {
                    DtoFile dtoArchivo = new DtoFile();
                    if (archivo instanceof MultipartFile) {
                        MultipartFile file = (MultipartFile) archivo;
                        dtoArchivo.setFile(file);

                        // Clasificar archivos según su tipo MIME o extensión
                        if (file.getContentType().equals("image/gif") || file.getOriginalFilename().endsWith(".gif")) {
                            dtoArchivo.setTipo("gif");
                        } else if (file.getContentType().startsWith("image")) {
                            dtoArchivo.setTipo("imagen");
                        } else if (file.getContentType().startsWith("video")) {
                            dtoArchivo.setTipo("video");
                        } else {
                            throw new RuntimeException("Tipo de archivo no soportado: " + file.getContentType());
                        }

                        dtoArchivo.setRutaArchivo(file.getOriginalFilename());
                    } else if (archivo instanceof String) {
                        String url = (String) archivo;
                        dtoArchivo.setRutaArchivo(url);

                        // Clasificar URLs basadas en la extensión
                        if (url.endsWith(".gif")) {
                            dtoArchivo.setTipo("gif");
                        } else if (url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".jpeg")||url.endsWith(".webp")) {
                            dtoArchivo.setTipo("imagen");
                        } else {
                            dtoArchivo.setTipo("video");
                        }
                    }
                    archivos.add(dtoArchivo);
                }
            }
            dtoPublication.setArchivos(archivos);

            // Llamar al método de negocio para actualizar la publicación
            businessPublication.updatePublication(dtoPublication);

            // Configurar la respuesta de éxito
            response.setType("success");
            response.setListMessage(List.of("Publicación actualizada correctamente"));
            response.setData(dtoPublication);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Configurar la respuesta de error
            response.setType("error");
            response.setListMessage(List.of("Error al actualizar la publicación: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/withfiles/paginated")
    public ResponseEntity<ResponseGetAllPublication> getPublicationsWithFiles(
            @RequestParam(defaultValue = "0") int page) {
        ResponseGetAllPublication response = new ResponseGetAllPublication();
        try {
            Page<DtoPublication> publications = businessPublication.getPublicationsWithFilesPageable(
                    PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "fechaRegistro")));
            response.setType("success");
            response.setData(publications.getContent());
            response.setListMessage(List.of("Publicaciones con archivos obtenidas correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener publicaciones con archivos: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/withfiles/career/paginated/{idCarrera}")
    public ResponseEntity<ResponseGetAllPublication> getPublicationsWithFilesByCareerPaginated(
            @PathVariable String idCarrera,
            @RequestParam(defaultValue = "0") int page) {
        ResponseGetAllPublication response = new ResponseGetAllPublication();
        try {
            Page<DtoPublication> publications = businessPublication.getPublicationsWithFilesByCareerPageable(
                    idCarrera, PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "fechaRegistro")));
            response.setType("success");
            response.setData(publications.getContent());
            response.setListMessage(List.of("Publicaciones con archivos por carrera obtenidas correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(
                    List.of("Error al obtener publicaciones con archivos por carrera: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/withoutfiles/paginated")
    public ResponseEntity<ResponseGetAllPublication> getPublicationsWithoutFiles(
            @RequestParam(defaultValue = "0") int page) {
        ResponseGetAllPublication response = new ResponseGetAllPublication();
        try {
            Page<DtoPublication> publications = businessPublication.getPublicationsWithoutFilesPageable(
                    PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "fechaRegistro")));
            response.setType("success");
            response.setData(publications.getContent());
            response.setListMessage(List.of("Publicaciones sin archivos obtenidas correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener publicaciones sin archivos: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/fix")
    public ResponseEntity<ResponseGeneric<String>> fixPublication(@RequestBody DtoFixPublication dtoFixPublication) {
        ResponseGeneric<String> response = new ResponseGeneric<>();
        try {
            businessPublication.fixPublication(dtoFixPublication);
            response.setType("success");
            response.setListMessage(List.of("Publicación actualizada correctamente"));
            response.setData("La publicación ha sido " + (dtoFixPublication.isFijada() ? "fijada" : "desfijada"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al actualizar la publicación: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/withoutfiles/career/paginated/{idCarrera}")
    public ResponseEntity<ResponseGetAllPublication> getPublicationsWithoutFilesByCareerPaginated(
            @PathVariable String idCarrera,
            @RequestParam(defaultValue = "0") int page) {
        ResponseGetAllPublication response = new ResponseGetAllPublication();
        try {
            Page<DtoPublication> publications = businessPublication.getPublicationsWithoutFilesByCareerPageable(
                    idCarrera, PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "fechaRegistro")));
            response.setType("success");
            response.setData(publications.getContent());
            response.setListMessage(List.of("Publicaciones sin archivos por carrera obtenidas correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(
                    List.of("Error al obtener publicaciones sin archivos por carrera: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/total")
    public ResponseEntity<ResponseGeneric<Long>> getTotalPublications() {
        ResponseGeneric<Long> response = new ResponseGeneric<>();
        try {
            long totalPublications = businessPublication.getTotalPublications();
            response.setType("success");
            response.setData(totalPublications);
            response.setListMessage(List.of("Total de publicaciones obtenidas correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener el total de publicaciones: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{idPublicacion}")
    public ResponseEntity<ResponseGeneric<String>> deletePublication(@PathVariable String idPublicacion) {
        ResponseGeneric<String> response = new ResponseGeneric<>();
        try {
            businessPublication.deletePublication(idPublicacion);
            response.setType("success");
            response.setListMessage(List.of("Publicación eliminada correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al eliminar la publicación: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
