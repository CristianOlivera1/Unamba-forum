package foro.Unamba_forum.Service.ReactionPublication;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import foro.Unamba_forum.Business.BusinessReactionPublication;
import foro.Unamba_forum.Dto.DtoReactionPublication;
import foro.Unamba_forum.Dto.DtoReactionSummary;
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

    @DeleteMapping("/remove")
    public ResponseEntity<ResponseGeneric<String>> removeReaction(@RequestParam String idUsuario, @RequestParam String idPublicacion) {
        ResponseGeneric<String> response = new ResponseGeneric<>();
        try {
            businessReaction.removeReaction(idUsuario, idPublicacion);

            response.setType("success");
            response.setListMessage(List.of("Reacción eliminada correctamente"));
            response.setData("Reacción eliminada");

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

    @GetMapping("/bytype")
    public ResponseEntity<ResponseGeneric<List<DtoReactionPublication>>> getReactionsByType(@RequestParam String idPublicacion, @RequestParam String tipo) {
        ResponseGeneric<List<DtoReactionPublication>> response = new ResponseGeneric<>();
        try {
            List<DtoReactionPublication> reactions = businessReaction.getReactionsByType(idPublicacion, tipo);

            response.setType("success");
            response.setListMessage(List.of("Reacciones obtenidas correctamente"));
            response.setData(reactions);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error inesperado, estamos trabajando para resolverlo. Gracias por su paciencia."));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/summary")
public ResponseEntity<ResponseGeneric<List<DtoReactionSummary>>> getReactionSummary(@RequestParam String idPublicacion) {
    ResponseGeneric<List<DtoReactionSummary>> response = new ResponseGeneric<>();
    try {
        List<DtoReactionSummary> summary = businessReaction.getReactionSummary(idPublicacion);

        response.setType("success");
        response.setListMessage(List.of("Resumen de reacciones obtenido correctamente"));
        response.setData(summary);

        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        e.printStackTrace();
        response.setType("exception");
        response.setListMessage(List.of("Ocurrió un error inesperado, estamos trabajando para resolverlo. Gracias por su paciencia."));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
}
