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
    
    public List<DtoCareer> getAllCareer() {
        return schoolRepository.findAll().stream()
                .map(this::convertToDto)
                .sorted((c1, c2) -> c1.getNombre().compareToIgnoreCase(c2.getNombre()))
                .collect(Collectors.toList());
    }

    private DtoCareer convertToDto(TCareer carrera) {
        DtoCareer dto = new DtoCareer();
        dto.setIdCarrera(carrera.getIdCarrera());
        dto.setNombre(carrera.getNombre());
        dto.setDescripcion(carrera.getDescripcion());
        dto.setLogo(carrera.getLogo());
        dto.setFechaRegistro(carrera.getFechaRegistro());
        return dto;
    }
    
    public long getTotalCareers() {
        return schoolRepository.count();
    }

    public DtoCareer getCareerById(String idCareer) {
        TCareer carrera = schoolRepository.findByIdCarrera(idCareer)
                .orElseThrow(() -> new RuntimeException("Carrera no encontrada: " + idCareer));
        return convertToDto(carrera);
    }
}
