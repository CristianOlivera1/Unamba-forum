package foro.Unamba_forum.Service.Career;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import foro.Unamba_forum.Business.BusinessCareer;
import foro.Unamba_forum.Dto.DtoCareer;

@RestController
@RequestMapping("/career")
public class CareerController {
    @Autowired
    private BusinessCareer businessSchool;

    //Get all careers
    @GetMapping("/getall")
    public List<DtoCareer> getAllCarreras() {
        return businessSchool.getAllCarreras();
    }
}
