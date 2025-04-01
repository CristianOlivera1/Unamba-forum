package foro.Unamba_forum.Service.Note;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import foro.Unamba_forum.Business.BusinessNote;
import foro.Unamba_forum.Dto.DtoNote;
import foro.Unamba_forum.Service.Note.RequestsObject.RequestsInsertNote;
import foro.Unamba_forum.Service.Note.ResponseObject.ResponseGetAllNote;
import foro.Unamba_forum.Service.Note.ResponseObject.ResponseInsertNote;

@RestController
@RequestMapping("/notes")
public class NoteController {
    @Autowired
    private BusinessNote businessNote;

@PostMapping("/insert")
public ResponseEntity<ResponseInsertNote> createNote(@ModelAttribute RequestsInsertNote request) {
    ResponseInsertNote response = new ResponseInsertNote();
    try {
        DtoNote createdNote = businessNote.createNote(
                request.getIdUsuario(),
                request.getContenido(),
                request.getBackgroundColor(),
                request.getRadialGradient()
        );

        response.setType("success");
        response.setData(createdNote); 
        response.setListMessage(List.of("Nota creada exitosamente"));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception e) {
        response.setType("error");
        response.setListMessage(List.of("Error al crear la nota: " + e.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

@GetMapping("/career/{idCarrera}")
public ResponseEntity<ResponseGetAllNote> getNotesByCareer(@PathVariable String idCarrera) {
    ResponseGetAllNote response = new ResponseGetAllNote();
    try {
        List<DtoNote> notes = businessNote.getNotesByCareer(idCarrera);
        response.setType("success");
        response.setData(notes);
        response.setListMessage(List.of("Notas obtenidas correctamente"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        response.setType("error");
        response.setListMessage(List.of("Error al obtener las notas: " + e.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

@GetMapping("/user/{idUsuario}")
public ResponseEntity<ResponseGetAllNote> getNotesByUser(@PathVariable String idUsuario) {
    ResponseGetAllNote response = new ResponseGetAllNote();
    try {
        List<DtoNote> notes = businessNote.getNotesByUser(idUsuario);
        response.setType("success");
        response.setData(notes);
        response.setListMessage(List.of("Notas obtenidas correctamente"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        response.setType("error");
        response.setListMessage(List.of("Error al obtener las notas: " + e.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

@DeleteMapping("/delete/{idNota}")
public ResponseEntity<ResponseInsertNote> deleteNoteById(@PathVariable String idNota) {
    ResponseInsertNote response = new ResponseInsertNote();
    try {
        businessNote.deleteNoteById(idNota);
        response.setType("success");
        response.setListMessage(List.of("Nota eliminada exitosamente"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        response.setType("error");
        response.setListMessage(List.of("Error al eliminar la nota: " + e.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
}
