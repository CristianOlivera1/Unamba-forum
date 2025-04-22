package foro.Unamba_forum.Service.ReactionComment;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import foro.Unamba_forum.Business.BusinessReactionComment;
import foro.Unamba_forum.Dto.DtoReactionComment;
import foro.Unamba_forum.Dto.DtoUserProfile;
import foro.Unamba_forum.Service.Generic.ResponseGeneric;
import foro.Unamba_forum.Service.ReactionComment.ResponseObject.ResponseInsertReactionC;
import foro.Unamba_forum.Service.ReactionComment.ResquestsObject.RequestsInsertReactionC;

@RestController
@RequestMapping("/reactioncomment")
public class ReactionCommentController {

    @Autowired
    private BusinessReactionComment businessReaction;

    @PostMapping("/insert")
    public ResponseEntity<ResponseInsertReactionC> addReaction(@ModelAttribute RequestsInsertReactionC request) {
        ResponseInsertReactionC response = new ResponseInsertReactionC();
        try {

            DtoReactionComment dtoReaction = new DtoReactionComment();
            dtoReaction.setIdComentario(request.getIdComentario());
            dtoReaction.setIdUsuario(request.getIdUsuario());
            dtoReaction.setIdRespuesta(request.getIdRespuesta());
            dtoReaction.setTipo(request.getTipo());

            businessReaction.addReaction(dtoReaction);

            response.setType("success");
            response.setListMessage(List.of("Reacción agregada correctamente"));
            response.setData(dtoReaction);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error al agregar la reacción"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*Actualizar una reaccion a un comentario o respuesta */
    @PutMapping("/update")
    public ResponseEntity<ResponseGeneric<DtoReactionComment>> updateReaction(
            @RequestParam String idUsuario,
            @RequestParam(required = false) String idComentario,
            @RequestParam(required = false) String idRespuesta,
            @RequestParam String nuevoTipo) {
        ResponseGeneric<DtoReactionComment> response = new ResponseGeneric<>();
        try {
            DtoReactionComment updatedReaction = businessReaction.updateReaction(idUsuario, idComentario, idRespuesta, nuevoTipo);
            response.setType("success");
            response.setListMessage(List.of("Reacción actualizada correctamente"));
            response.setData(updatedReaction);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al actualizar la reacción: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Total de reacciones de un comentario
    @GetMapping("/count/comment/{idComentario}")
    public ResponseEntity<ResponseGeneric<Long>> countReactionsByComment(@PathVariable String idComentario) {
        ResponseGeneric<Long> response = new ResponseGeneric<>();
        try {
            long count = businessReaction.countReactionsByComment(idComentario);

            response.setType("success");
            response.setData(count);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Error al contar las reacciones del comentario"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Total de reacciones de una respuesta
    @GetMapping("/count/response/{idRespuesta}")
    public ResponseEntity<ResponseGeneric<Long>> countReactionsByResponse(@PathVariable String idRespuesta) {
        ResponseGeneric<Long> response = new ResponseGeneric<>();
        try {
            long count = businessReaction.countReactionsByResponse(idRespuesta);

            response.setType("success");
            response.setData(count);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Error al contar las reacciones de la respuesta"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{idComentario}/{tipo}")
    public ResponseEntity<ResponseGeneric<List<DtoUserProfile>>> getUsersByReactionType(
            @PathVariable String idComentario, @PathVariable String tipo) {
        ResponseGeneric<List<DtoUserProfile>> response = new ResponseGeneric<>();
        try {
            List<DtoUserProfile> users = businessReaction.getUsersByReactionType(idComentario, tipo);

            response.setListMessage(List.of("Usuarios obtenidos por tipo de reaccion"));
            response.setType("success");
            response.setData(users);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            response.setType("error");
            response.setListMessage(List.of(e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error al obtener los usuarios por tipo de reacción"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/response/{idRespuesta}/{tipo}")
    public ResponseEntity<ResponseGeneric<List<DtoUserProfile>>> getUsersByReactionTypeForResponses(
        @PathVariable String idRespuesta, @PathVariable String tipo) {
        ResponseGeneric<List<DtoUserProfile>> response = new ResponseGeneric<>();
        try {
        List<DtoUserProfile> users = businessReaction.getUsersByReactionTypeForResponses(idRespuesta, tipo);

        response.setListMessage(List.of("Usuarios obtenidos por tipo de reacción en respuestas"));
        response.setType("success");
        response.setData(users);
        return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
        response.setType("error");
        response.setListMessage(List.of(e.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
        e.printStackTrace();
        response.setType("exception");
        response.setListMessage(List.of("Ocurrió un error al obtener los usuarios por tipo de reacción en respuestas"));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Obtener reacciones de un usuario a comentarios de una publicación    
    @GetMapping("/userreactions")
    public ResponseEntity<ResponseGeneric<List<DtoReactionComment>>> getReactionsByUserAndPublication(
            @RequestParam String idUsuario, @RequestParam String idPublicacion) {
        ResponseGeneric<List<DtoReactionComment>> response = new ResponseGeneric<>();
        try {
            List<DtoReactionComment> reactions = businessReaction.getReactionsByUserAndPublication(idUsuario, idPublicacion);
            response.setType("success");
            response.setListMessage(List.of("Reacciones obtenidas correctamente"));
            response.setData(reactions);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener las reacciones: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/remove/{idReaction}")
    public ResponseEntity<ResponseGeneric<String>> removeReaction(@PathVariable String idReaction) {
        ResponseGeneric<String> response = new ResponseGeneric<>();
        try {
            businessReaction.removeReaction(idReaction);

            response.setType("success");
            response.setListMessage(List.of("Reacción eliminada correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            response.setType("error");
            response.setListMessage(List.of(e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error al eliminar la reacción"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
