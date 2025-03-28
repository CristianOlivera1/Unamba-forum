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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import foro.Unamba_forum.Business.BusinessReactionComment;
import foro.Unamba_forum.Dto.DtoReactionComment;
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
            // Check if the user has already reacted to the same comment
            boolean alreadyReacted = businessReaction.hasUserReactedToComment(request.getIdUsuario(), request.getIdComentario());
            if (alreadyReacted) {
                response.setType("error");
                response.setListMessage(List.of("El usuario ya ha reaccionado a este comentario"));
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

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
