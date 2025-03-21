package foro.Unamba_forum.Service.Category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import foro.Unamba_forum.Business.BusinessCategory;
import foro.Unamba_forum.Dto.DtoCategory;
import foro.Unamba_forum.Service.Category.ResponseObject.ResponseGetAllCategories;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private BusinessCategory businessCategory;

    @GetMapping("/getall")
    public ResponseEntity<ResponseGetAllCategories> getAllCategorias() {
        ResponseGetAllCategories response = new ResponseGetAllCategories();

        try {
            List<DtoCategory> listDtoCategory = businessCategory.getAllCategorias();
            response.setData(listDtoCategory);
            response.setType("success");
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Ocurrió un error inesperado."));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
