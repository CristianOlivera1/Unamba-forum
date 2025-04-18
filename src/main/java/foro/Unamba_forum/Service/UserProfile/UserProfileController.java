package foro.Unamba_forum.Service.UserProfile;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import foro.Unamba_forum.Business.BusinessUserProfile;
import foro.Unamba_forum.Dto.DtoDetailProfile;
import foro.Unamba_forum.Dto.DtoUserProfile;
import foro.Unamba_forum.Dto.DtoUserProfileHover;
import foro.Unamba_forum.Service.Generic.ResponseGeneric;
import foro.Unamba_forum.Service.UserProfile.RequestObject.RequestInsertUserProfile;
import foro.Unamba_forum.Service.UserProfile.RequestObject.RequestUpdateUserProfile;
import foro.Unamba_forum.Service.UserProfile.ResponseObject.ResponseInsertUserProfile;
import foro.Unamba_forum.Service.UserProfile.ResponseObject.ResponseUpdateUserProfile;

@RestController

@RequestMapping("/userprofile")
public class UserProfileController {
    @Autowired
    private BusinessUserProfile businessUserProfile;

    @PostMapping(path = "/insert", consumes = { "multipart/form-data" })
    public ResponseEntity<ResponseInsertUserProfile> insert(
            @ModelAttribute RequestInsertUserProfile request,
            @RequestParam(value = "fotoPerfil", required = false) MultipartFile fotoPerfil,
            @RequestParam(value = "fotoPortada", required = false) MultipartFile fotoPortada) {
        ResponseInsertUserProfile response = new ResponseInsertUserProfile();
        try {
            DtoUserProfile dtoUserProfile = new DtoUserProfile();
            dtoUserProfile.setIdUsuario(request.getIdUsuario());
            dtoUserProfile.setIdCarrera(request.getIdCarrera());
            dtoUserProfile.setNombre(request.getNombre());
            dtoUserProfile.setApellidos(request.getApellidos());
            dtoUserProfile.setDescripcion(request.getDescripcion());
            dtoUserProfile.setFechaNacimiento(request.getFechaNacimiento());
            dtoUserProfile.setGenero(request.getGenero());
            dtoUserProfile.setFotoPerfil(fotoPerfil != null ? fotoPerfil.getOriginalFilename() : null);
            dtoUserProfile.setFotoPortada(fotoPortada != null ? fotoPortada.getOriginalFilename() : null);

            businessUserProfile.insert(dtoUserProfile, fotoPerfil, fotoPortada);

            response.setType("success");
            response.setListMessage(List.of("Perfil de usuario creado correctamente"));
            response.setData(dtoUserProfile);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al crear el perfil de usuario: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<ResponseGeneric<List<DtoUserProfile>>> getAll() {
        ResponseGeneric<List<DtoUserProfile>> response = new ResponseGeneric<>();
        try {
            List<DtoUserProfile> dtoUserProfiles = businessUserProfile.getAll();
            response.setType("success");
            response.setData(dtoUserProfiles);
            response.setListMessage(List.of("Perfiles de usuario obtenidos correctamente"));

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener los perfiles de usuario: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getprofilebyuser/{idUsuario}")
    public ResponseEntity<ResponseGeneric<DtoUserProfile>> getByIdUsuario(@PathVariable String idUsuario) {
        ResponseGeneric<DtoUserProfile> response = new ResponseGeneric<>();
        try {
            DtoUserProfile dtoUserProfile = businessUserProfile.getByIdUsuario(idUsuario);
            if (dtoUserProfile != null) {
                response.setType("success");
                response.setListMessage(
                        List.of("Perfil de usuario obtenido correctamente: " + dtoUserProfile.getNombre()));
                response.setData(dtoUserProfile);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setType("error");
                response.setListMessage(List.of("Perfil de usuario no encontrado"));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener el perfil de usuario: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getprofilebyprofile/{idPerfil}")
    public ResponseEntity<ResponseGeneric<DtoUserProfile>> getById(@PathVariable String idPerfil) {
        ResponseGeneric<DtoUserProfile> response = new ResponseGeneric<>();
        try {
            DtoUserProfile dtoUserProfile = businessUserProfile.getById(idPerfil);
            if (dtoUserProfile != null) {
                response.setType("success");
                response.setListMessage(
                        List.of("Perfil de usuario obtenido correctamente: " + dtoUserProfile.getNombre()));
                response.setData(dtoUserProfile);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setType("error");
                response.setListMessage(List.of("Perfil de usuario no encontrado"));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener el perfil de usuario: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/hover/{idUsuario}")
    public ResponseEntity<ResponseGeneric<DtoUserProfileHover>> getUserProfileHover(@PathVariable String idUsuario) {
        ResponseGeneric<DtoUserProfileHover> response = new ResponseGeneric<>();
        try {
            DtoUserProfileHover userProfileHover = businessUserProfile.getUserProfileHover(idUsuario);
            response.setType("success");
            response.setListMessage(List.of("Información del perfil obtenida correctamente"));
            response.setData(userProfileHover);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener la información del perfil: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/detail/{idUsuario}")
    public ResponseEntity<ResponseGeneric<DtoDetailProfile>> getDetailProfile(@PathVariable String idUsuario) {
        ResponseGeneric<DtoDetailProfile> response = new ResponseGeneric<>();
        try {
            DtoDetailProfile detailProfile = businessUserProfile.getDetailProfile(idUsuario);
            response.setType("success");
            response.setListMessage(List.of("Detalles del perfil obtenidos correctamente"));
            response.setData(detailProfile);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener los detalles del perfil: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/update", consumes = { "multipart/form-data" })
    public ResponseEntity<ResponseUpdateUserProfile> update(
            @ModelAttribute RequestUpdateUserProfile request,
            @RequestParam(value = "fotoPerfil", required = false) MultipartFile fotoPerfil,
            @RequestParam(value = "fotoPortada", required = false) MultipartFile fotoPortada) {

        ResponseUpdateUserProfile response = new ResponseUpdateUserProfile();

        try {
            DtoUserProfile dtoUserProfile = new DtoUserProfile();
            dtoUserProfile.setIdUsuario(request.getIdUsuario());
            dtoUserProfile.setIdCarrera(request.getIdCarrera());
            dtoUserProfile.setNombre(request.getNombre());
            dtoUserProfile.setApellidos(request.getApellidos());
            dtoUserProfile.setDescripcion(request.getDescripcion());
            dtoUserProfile.setFotoPerfil(fotoPerfil != null ? fotoPerfil.getOriginalFilename() : null);
            dtoUserProfile.setFotoPortada(fotoPortada != null ? fotoPortada.getOriginalFilename() : null);
            dtoUserProfile.setFechaNacimiento(request.getFechaNacimiento());
            dtoUserProfile.setGenero(request.getGenero());
            businessUserProfile.update(dtoUserProfile, fotoPerfil, fotoPortada);

            response.setType("success");
            response.setListMessage(List.of("Perfil de usuario actualizado correctamente"));
            response.setData(dtoUserProfile);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al actualizar el perfil de usuario: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{idPerfil}")
    public ResponseEntity<ResponseGeneric<String>> delete(@PathVariable String idPerfil) {
        ResponseGeneric<String> response = new ResponseGeneric<>();
        try {
            businessUserProfile.delete(idPerfil);
            response.setType("success");
            response.setListMessage(List.of("Perfil de usuario eliminado correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al eliminar el perfil de usuario: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
