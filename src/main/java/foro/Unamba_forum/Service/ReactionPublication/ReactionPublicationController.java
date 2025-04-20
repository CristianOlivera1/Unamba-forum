package foro.Unamba_forum.Service.ReactionPublication;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import foro.Unamba_forum.Business.BusinessReactionPublication;
import foro.Unamba_forum.Dto.DtoReactionAndCommentSummary;
import foro.Unamba_forum.Dto.DtoReactionPublication;
import foro.Unamba_forum.Dto.DtoUserProfile;
import foro.Unamba_forum.Service.Generic.ResponseGeneric;

@RestController

@RequestMapping("/reaction")
public class ReactionPublicationController {
    
    @Autowired
    private BusinessReactionPublication businessReaction;

    @PostMapping("/insert")
    public ResponseEntity<ResponseGeneric<DtoReactionPublication>> addReaction(@ModelAttribute DtoReactionPublication dtoReaction) {
        ResponseGeneric<DtoReactionPublication> response = new ResponseGeneric<>();
        try {
            if (businessReaction.hasUserReacted(dtoReaction.getIdUsuario(), dtoReaction.getIdPublicacion())) {
                response.setType("error");
                response.setListMessage(List.of("El usuario ya ha reaccionado a esta publicación"));
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

            businessReaction.addReaction(dtoReaction);

            response.setType("success");
            response.setListMessage(List.of("Reacción agregada correctamente"));
            response.setData(dtoReaction);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error inesperado, estamos trabajando para resolverlo. Gracias por su paciencia."));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseGeneric<Void>> deleteReaction(
            @RequestParam String idUsuario, @RequestParam String idPublicacion) {
        ResponseGeneric<Void> response = new ResponseGeneric<>();
        try {
    
            businessReaction.deleteReaction(idUsuario, idPublicacion);
    
            response.setType("success");
            response.setListMessage(List.of("Reacción eliminada correctamente"));
    
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            response.setType("error");
            response.setListMessage(List.of(e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Error al eliminar la reacción"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
   
    @PutMapping("/update")
    public ResponseEntity<ResponseGeneric<DtoReactionPublication>> updateReaction(
            @RequestParam String idUsuario,
            @RequestParam String idPublicacion,
            @RequestParam String nuevoTipo) {
        ResponseGeneric<DtoReactionPublication> response = new ResponseGeneric<>();
        try {
            // Llamar al método de negocio para actualizar la reacción
            DtoReactionPublication updatedReaction = businessReaction.updateReaction(idUsuario, idPublicacion, nuevoTipo);
    
            response.setType("success");
            response.setListMessage(List.of("Reacción actualizada correctamente"));
            response.setData(updatedReaction);
    
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Error al actualizar la reacción"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseGeneric<DtoReactionPublication>> getCurrentReaction(
        @RequestParam String idUsuario, @RequestParam String idPublicacion) {
        ResponseGeneric<DtoReactionPublication> response = new ResponseGeneric<>();
        try {
        DtoReactionPublication reaction = businessReaction.getReaction(idUsuario, idPublicacion);

        if (reaction == null) {
            response.setType("info");
            response.setListMessage(List.of("El usuario no ha reaccionado a esta publicación"));
            response.setType("info");
            response.setListMessage(List.of("No hay reacciones para esta publicación"));
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setType("success");
        response.setListMessage(List.of("Reacción obtenida correctamente"));
        response.setData(reaction);

        return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
        e.printStackTrace();
        response.setType("exception");
        response.setListMessage(List.of("Ocurrió un error inesperado, estamos trabajando para resolverlo. Gracias por su paciencia."));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/countbytype")
    public ResponseEntity<ResponseGeneric<Long>> getReactionCountByType(@RequestParam String idPublicacion, @RequestParam String tipo) {
        ResponseGeneric<Long> response = new ResponseGeneric<>();
        try {
            long count = businessReaction.getReactionCountByType(idPublicacion, tipo);

            response.setType("success");
            response.setListMessage(List.of("Cantidad de reacciones obtenida correctamente"));
            response.setData(count);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error inesperado, estamos trabajando para resolverlo. Gracias por su paciencia."));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/total")
    public ResponseEntity<ResponseGeneric<Long>> getTotalReactions(@RequestParam String idPublicacion) {
        ResponseGeneric<Long> response = new ResponseGeneric<>();
        try {
            long total = businessReaction.getTotalReactions(idPublicacion);

            response.setType("success");
            response.setListMessage(List.of("Total de reacciones obtenidas correctamente"));
            response.setData(total);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error inesperado, estamos trabajando para resolverlo. Gracias por su paciencia."));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

@GetMapping("/reactionuser")
public ResponseEntity<ResponseGeneric<List<DtoUserProfile>>> getReactionUsers(
        @RequestParam String idPublicacion,
        @RequestParam String tipo) {
    ResponseGeneric<List<DtoUserProfile>> response = new ResponseGeneric<>();
    try {
        List<DtoUserProfile> usuarios = businessReaction.getUsersByReactionType(idPublicacion, tipo);
        response.setType("success");
        response.setListMessage(List.of("Usuarios obtenidos correctamente"));
        response.setData(usuarios);
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        response.setType("error");
        response.setListMessage(List.of("Error al obtener los usuarios"));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

@GetMapping("/reactioncommentsummary")
public ResponseEntity<ResponseGeneric<DtoReactionAndCommentSummary>> getReactionAndCommentSummary(
        @RequestParam String idPublicacion) {
    ResponseGeneric<DtoReactionAndCommentSummary> response = new ResponseGeneric<>();
    try {
        DtoReactionAndCommentSummary summary = businessReaction.getReactionAndCommentSummary(idPublicacion);
        response.setType("success");
        response.setListMessage(List.of("Resumen de reacciones y comentarios obtenido correctamente"));
        response.setData(summary);
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        response.setType("error");
        response.setListMessage(List.of("Error al obtener el resumen de reacciones y comentarios"));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

}
