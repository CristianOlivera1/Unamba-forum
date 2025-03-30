package foro.Unamba_forum.Service.Totals;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import foro.Unamba_forum.Business.BusinessTotals;
import foro.Unamba_forum.Dto.DtoTotals;
import foro.Unamba_forum.Service.Generic.ResponseGeneric;

@RestController
@RequestMapping("/totals")
public class TotalsController {
    
    @Autowired
    private BusinessTotals businessTotals;

@GetMapping("/hero")
public ResponseEntity<ResponseGeneric<DtoTotals>> getTotals() {
    ResponseGeneric<DtoTotals> response = new ResponseGeneric<>();
    try {
        DtoTotals totals = businessTotals.getTotals();
        response.setType("success");
        response.setData(totals);
        response.setListMessage(List.of("Totales obtenidos correctamente."));
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        response.setType("error");
        response.setListMessage(List.of("Error al obtener los totales: " + e.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
    
}
