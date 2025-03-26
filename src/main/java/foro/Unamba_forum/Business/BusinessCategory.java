package foro.Unamba_forum.Business;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoCategory;
import foro.Unamba_forum.Entity.TCategory;
import foro.Unamba_forum.Repository.RepoCategory;

@Service
public class BusinessCategory {
    @Autowired
    private RepoCategory repoCategory;

    public List<DtoCategory> getAllCategorias() {
        return repoCategory.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private DtoCategory convertToDto(TCategory categoria) {
        DtoCategory dto = new DtoCategory();
        dto.setIdCategoria(categoria.getIdCategoria());
        dto.setNombre(categoria.getNombre());
        dto.setFechaRegistro(categoria.getFechaRegistro());
        return dto;
    }
    
}
