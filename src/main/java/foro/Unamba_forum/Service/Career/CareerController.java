package foro.Unamba_forum.Service.Career;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/total")
    public ResponseEntity<ResponseGeneric<Long>>  getTotalCareers() {
        ResponseGeneric<Long> response = new ResponseGeneric<>();
        try {
            long totalCareers = businessCareer.getAllCareer().size();
            response.setType("success");
            response.setData(totalCareers);
            response.setListMessage(List.of("Total de carreras obtenidas correctamente."));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error inesperado."));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
