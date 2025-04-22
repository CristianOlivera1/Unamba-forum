package foro.Unamba_forum.Service.CommentPublication;

import java.util.List;

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

import foro.Unamba_forum.Business.BusinessCommentPublication;
import foro.Unamba_forum.Dto.DtoCommentPublication;
import foro.Unamba_forum.Dto.DtoUserComment;
import foro.Unamba_forum.Service.CommentPublication.RequestsObject.RequestUpdateCP;
import foro.Unamba_forum.Service.CommentPublication.RequestsObject.RequestsInsertCP;
import foro.Unamba_forum.Service.CommentPublication.ResponseObject.ResponseGetAllCP;
import foro.Unamba_forum.Service.CommentPublication.ResponseObject.ResponseInsertCP;
import foro.Unamba_forum.Service.CommentPublication.ResponseObject.ResponseUpdateCP;
import foro.Unamba_forum.Service.Generic.ResponseGeneric;

@RestController

@RequestMapping("/commentpublication")
public class CommentPublicationController {

    @Autowired
    private BusinessCommentPublication businessComment;

    @GetMapping("/hierarchy/{idPublicacion}")
    public ResponseEntity<List<DtoCommentPublication>> getCommentHierarchy(@PathVariable String idPublicacion) {
        List<DtoCommentPublication> comments = businessComment.getCommentsByPublication(idPublicacion);
        comments.forEach(comment -> {
            // comment.setReacciones(businessReaction.countReactionsByComment(comment.getIdComentario()));
            // comment.setRespuestas(businessResponse.getResponsesByComment(comment.getIdComentario()));
        });
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    // Endpoint para agregar un comentario
    @PostMapping("/insert")
    public ResponseEntity<ResponseInsertCP> addComment(@ModelAttribute RequestsInsertCP request) {
        ResponseInsertCP response = new ResponseInsertCP();
        try {
            // Validación de los datos
            if (request.getIdUsuario() == null || request.getIdPublicacion() == null
                    || request.getContenido().isEmpty()) {
                response.setType("error");
                response.setListMessage(List.of("Datos insuficientes para agregar el comentario"));
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            DtoCommentPublication dtoComment = new DtoCommentPublication();
            dtoComment.setIdUsuario(request.getIdUsuario());
            dtoComment.setIdPublicacion(request.getIdPublicacion());
            dtoComment.setContenido(request.getContenido());

            businessComment.addComment(dtoComment);

            response.setType("success");
            response.setListMessage(List.of("Comentario agregado correctamente"));
            response.setData(dtoComment);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error al agregar el comentario"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /*usuarios que comentaron en una publicacion */
     @GetMapping("/users/{idPublicacion}")
    public ResponseEntity<ResponseGeneric<List<DtoUserComment>>> getUsersWhoCommented(
            @PathVariable String idPublicacion) {
        ResponseGeneric<List<DtoUserComment>> response = new ResponseGeneric<>();
        try {
            List<DtoUserComment> users = businessComment.getUsersWhoCommented(idPublicacion);
            response.setType("success");
            response.setListMessage(List.of("Usuarios que comentaron obtenidos correctamente"));
            response.setData(users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener los usuarios: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener todos los comentarios de una publicación con sus usuarios
    @GetMapping("/list/{idPublicacion}")
    public ResponseEntity<ResponseGetAllCP> getCommentsByPublication(@PathVariable String idPublicacion) {
        ResponseGetAllCP response = new ResponseGetAllCP();
        try {
            List<DtoCommentPublication> comments = businessComment.getCommentsByPublication(idPublicacion);

            response.setType("success");
            response.setListMessage(List.of("Comentarios obtenidos correctamente"));
            response.setData(comments);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error al obtener los comentarios"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener el total de comentarios de una publicación
    @GetMapping("/total/{idPublicacion}")
    public ResponseEntity<ResponseGeneric<Long>> getTotalComments(@PathVariable String idPublicacion) {
        ResponseGeneric<Long> response = new ResponseGeneric<>();
        try {
            long totalComments = businessComment.getTotalComments(idPublicacion);

            response.setType("success");
            response.setListMessage(List.of("Total de comentarios obtenidos correctamente"));
            response.setData(totalComments);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error al obtener el total de comentarios"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para actualizar un comentario
    @PutMapping("/update")
    public ResponseEntity<ResponseUpdateCP> updateComment(@ModelAttribute RequestUpdateCP request) {
        ResponseUpdateCP response = new ResponseUpdateCP();
        try {
            // Validación de entrada
            if (request.getIdComentario() == null || request.getIdComentario().isEmpty()
                    || request.getContenido() == null || request.getContenido().isEmpty()) {
                response.setType("error");
                response.setListMessage(List.of("Datos insuficientes para actualizar el comentario"));
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            DtoCommentPublication updatedComment = businessComment.updateComment(request);

            response.setType("success");
            response.setListMessage(List.of("Comentario actualizado correctamente"));
            response.setData(updatedComment);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error al actualizar el comentario"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para eliminar un comentario
    @DeleteMapping("/delete/{idComentario}")
    public ResponseEntity<ResponseGeneric<String>> deleteComment(@PathVariable String idComentario) {
        ResponseGeneric<String> response = new ResponseGeneric<>();
        try {
            businessComment.deleteComment(idComentario);

            response.setType("success");
            response.setListMessage(List.of("Comentario eliminado correctamente"));
            response.setData("Comentario eliminado con éxito");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error al eliminar el comentario"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
