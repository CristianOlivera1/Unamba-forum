package foro.Unamba_forum.Service.Career;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import foro.Unamba_forum.Business.BusinessCareer;
import foro.Unamba_forum.Dto.DtoCareer;
import foro.Unamba_forum.Service.Career.ResponseObject.ResponseGetAllCareer;
import foro.Unamba_forum.Service.Generic.ResponseGeneric;

@RestController
@RequestMapping("/career")
public class CareerController {
    @Autowired
    private BusinessCareer businessCareer;

    @GetMapping("/getall")
    public ResponseEntity<ResponseGetAllCareer> getAllCareer() {
        ResponseGetAllCareer response = new ResponseGetAllCareer();

        try {
            List<DtoCareer> listDtoCareer = businessCareer.getAllCareer();
            response.setData(listDtoCareer);
            response.setType("success");
            response.setListMessage(List.of("Datos obtenidos correctamente."));

        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Ocurrió un error inesperado."));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{idCarrera}")
    public ResponseEntity<ResponseGeneric<DtoCareer>> getCareerById(@PathVariable String idCarrera) {
        ResponseGeneric<DtoCareer> response = new ResponseGeneric<>();
        try {
            DtoCareer career = businessCareer.getCareerById(idCarrera);
            response.setType("success");
            response.setData(career);
            response.setListMessage(List.of("Carrera obtenida correctamente."));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.setType("error");
            response.setListMessage(List.of(e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error inesperado."));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
