package foro.Unamba_forum.Business;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoCareer;
import foro.Unamba_forum.Entity.TCareer;
import foro.Unamba_forum.Repository.RepoCareer;

@Service
public class BusinessCareer {
    @Autowired
    private RepoCareer schoolRepository;
    
    //Get all careers
    public List<DtoCareer> getAllCareer() {
        return schoolRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private DtoCareer convertToDto(TCareer carrera) {
        DtoCareer dto = new DtoCareer();
        dto.setIdCarrera(carrera.getIdCarrera());
        dto.setNombre(carrera.getNombre());
        dto.setFechaRegistro(carrera.getFechaRegistro());
        return dto;
    }
}
