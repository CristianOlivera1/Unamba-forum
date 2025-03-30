package foro.Unamba_forum.Service.ResponseComment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import foro.Unamba_forum.Business.BusinessResponseComment;
import foro.Unamba_forum.Dto.DtoResponseComment;
import foro.Unamba_forum.Service.ResponseComment.RequestsObject.RequestsInsertRC;
import foro.Unamba_forum.Service.ResponseComment.ResponseObject.ResponseGetAllRC;
import foro.Unamba_forum.Service.ResponseComment.ResponseObject.ResponseInsertRC;

@RestController

@RequestMapping("/responsecomment")
public class ResponseCommentController {
    
    @Autowired
    private BusinessResponseComment businessResponse;

    // Endpoint para agregar una respuesta
    @PostMapping("/insert")
    public ResponseEntity<ResponseInsertRC> addResponse(@ModelAttribute RequestsInsertRC request) {
        ResponseInsertRC response = new ResponseInsertRC();
        try {
    
            if (request.getIdUsuario() == null || request.getContenido() == null ||
                (request.getIdComentario() == null && request.getIdRespuestaPadre() == null)) {
                response.setType("error");
                response.setListMessage(List.of("Datos insuficientes para agregar la respuesta"));
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
    
            DtoResponseComment dtoResponse = new DtoResponseComment();
            dtoResponse.setIdComentario(request.getIdComentario());
            dtoResponse.setIdRespuestaPadre(request.getIdRespuestaPadre());
            dtoResponse.setIdUsuario(request.getIdUsuario());
            dtoResponse.setContenido(request.getContenido());
    
            businessResponse.addResponse(dtoResponse);
    
            response.setType("success");
            response.setListMessage(List.of("Respuesta agregada correctamente"));
            response.setData(dtoResponse);
    
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            response.setType("error");
            response.setListMessage(List.of(e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error al agregar la respuesta"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener respuestas de un comentario
    @GetMapping("/list/{idComentario}")
    public ResponseEntity<ResponseGetAllRC> getResponsesByComment(@PathVariable String idComentario) {
        ResponseGetAllRC response = new ResponseGetAllRC();
        try {
            List<DtoResponseComment> responses = businessResponse.getResponsesByCommentWithDetails(idComentario);

            response.setType("success");
            response.setListMessage(List.of("Respuestas obtenidas correctamente"));
            response.setData(responses);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error al obtener las respuestas"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener respuestas hijas de una respuesta
    @GetMapping("/list/child/{idRespuestaPadre}")
    public ResponseEntity<ResponseGetAllRC> getResponsesByParent(@PathVariable String idRespuestaPadre) {
        ResponseGetAllRC response = new ResponseGetAllRC();
        try {
            List<DtoResponseComment> responses = businessResponse.getResponsesByParent(idRespuestaPadre);

            response.setType("success");
            response.setListMessage(List.of("Respuestas hijas obtenidas correctamente"));
            response.setData(responses);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error al obtener las respuestas hijas"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    } 
}
