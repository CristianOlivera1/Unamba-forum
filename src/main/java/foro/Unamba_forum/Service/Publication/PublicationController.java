package foro.Unamba_forum.Service.Publication;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import foro.Unamba_forum.Business.BusinessPublication;
import foro.Unamba_forum.Dto.DtoFile;
import foro.Unamba_forum.Dto.DtoPublication;
import foro.Unamba_forum.Service.Generic.ResponseGeneric;
import foro.Unamba_forum.Service.Publication.RequestsObject.RequestInsertPublication;
import foro.Unamba_forum.Service.Publication.RequestsObject.RequestUpdatePublication;
import foro.Unamba_forum.Service.Publication.ResponseObject.ResponseGetAllPublication;
import foro.Unamba_forum.Service.Publication.ResponseObject.ResponseInsertPublication;
import foro.Unamba_forum.Service.Publication.ResponseObject.ResponseUpdatePublication;

@RestController

@RequestMapping("/publication")
public class PublicationController {
    
    @Autowired
    private BusinessPublication businessPublication;

    @PostMapping("/insert")
    public ResponseEntity<ResponseInsertPublication> insertPublication(@ModelAttribute RequestInsertPublication request) {
        ResponseInsertPublication response = new ResponseInsertPublication();
        try {
            DtoPublication dtoPublication = new DtoPublication();
            dtoPublication.setIdUsuario(request.getIdUsuario());
            dtoPublication.setIdCategoria(request.getIdCategoria());
            dtoPublication.setIdCarrera(request.getIdCarrera());
            dtoPublication.setTitulo(request.getTitulo());
            dtoPublication.setContenido(request.getContenido());
    
            // Procesar archivos solo si se proporcionan
            List<DtoFile> archivos = new ArrayList<>();
            if (request.getArchivos() != null && !request.getArchivos().isEmpty()) {
                archivos = request.getArchivos().stream().map(file -> {
                    DtoFile dtoArchivo = new DtoFile();
                    dtoArchivo.setFile(file); // Asignar el archivo al DTO
                    dtoArchivo.setTipo(file.getContentType().startsWith("image") ? "imagen" : "video");
                    dtoArchivo.setRutaArchivo(file.getOriginalFilename()); // Aquí puedes manejar la subida de archivos
                    return dtoArchivo;
                }).collect(Collectors.toList());
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

    @PutMapping("/update")
    public ResponseEntity<ResponseUpdatePublication> updatePublication(@ModelAttribute RequestUpdatePublication request) {
        ResponseUpdatePublication response = new ResponseUpdatePublication();
        try {
            DtoPublication dtoPublication = new DtoPublication();
            dtoPublication.setIdPublicacion(request.getIdPublicacion());
            dtoPublication.setIdCategoria(request.getIdCategoria());
            dtoPublication.setTitulo(request.getTitulo());
            dtoPublication.setContenido(request.getContenido());
    
            // Procesar archivos solo si se proporcionan
            List<DtoFile> archivos = new ArrayList<>();
            if (request.getArchivos() != null && !request.getArchivos().isEmpty()) {
                archivos = request.getArchivos().stream().map(file -> {
                    DtoFile dtoArchivo = new DtoFile();
                    dtoArchivo.setFile(file); // Asignar el archivo al DTO
                    dtoArchivo.setTipo(file.getContentType().startsWith("image") ? "imagen" : "video");
                    dtoArchivo.setRutaArchivo(file.getOriginalFilename()); // Aquí puedes manejar la subida del archivo
                    return dtoArchivo;
                }).collect(Collectors.toList());
            }
            dtoPublication.setArchivos(archivos);
    
            businessPublication.updatePublication(dtoPublication);
    
            response.setType("success");
            response.setListMessage(List.of("Publicación actualizada correctamente"));
            response.setData(dtoPublication);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al actualizar la publicación: " + e.getMessage()));
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

    @GetMapping("/withfiles")
    public ResponseEntity<ResponseGetAllPublication> getPublicationsWithFiles() {
        ResponseGetAllPublication response = new ResponseGetAllPublication();
        try {
            List<DtoPublication> publications = businessPublication.getPublicationsWithFiles();
            response.setType("success");
            response.setData(publications);
            response.setListMessage(List.of("Publicaciones con archivos obtenidas correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener publicaciones con archivos: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/withoutfiles")
    public ResponseEntity<ResponseGetAllPublication> getPublicationsWithoutFiles() {
        ResponseGetAllPublication response = new ResponseGetAllPublication();
        try {
            List<DtoPublication> publications = businessPublication.getPublicationsWithoutFiles();
            response.setType("success");
            response.setData(publications);
            response.setListMessage(List.of("Publicaciones sin archivos obtenidas correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener publicaciones sin archivos: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
