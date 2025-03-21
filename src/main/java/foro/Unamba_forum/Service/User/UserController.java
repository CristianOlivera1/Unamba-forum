package foro.Unamba_forum.Service.User;

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

import foro.Unamba_forum.Business.BusinessUser;
import foro.Unamba_forum.Dto.DtoUser;
import foro.Unamba_forum.Service.Generic.ResponseGeneric;
import foro.Unamba_forum.Service.User.RequestsObject.RequestUpdate;
import foro.Unamba_forum.Service.User.ResponseObject.ResponseDelete;
import foro.Unamba_forum.Service.User.ResponseObject.ResponseGetAllUsers;
import foro.Unamba_forum.Service.User.ResponseObject.ResponseUpdate;

@RestController

@RequestMapping("/user")
public class UserController {
    @Autowired
    private BusinessUser businessUser;

        @PostMapping("/insert")
        public ResponseEntity<ResponseGeneric<String>> insert(@RequestParam String email, @RequestParam String contrasenha) {
            ResponseGeneric<String> response = new ResponseGeneric<>();
            try {
                if (businessUser.emailExists(email)) {
                    response.setType("error");
                    response.setListMessage(List.of("El nombre de usuario ya existe"));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

                DtoUser dtoUser = new DtoUser();
                dtoUser.setEmail(email);
                dtoUser.setContrasenha(contrasenha);

                businessUser.insert(dtoUser);

                response.setType("success");
                response.setListMessage(List.of("Registro realizado correctamente"));
                return new ResponseEntity<>(response, HttpStatus.CREATED);

            } catch (Exception e) {
                e.printStackTrace();
                response.setType("exception");
                response.setListMessage(List.of("Ocurrió un error inesperado, estamos trabajando para resolverlo. Gracias por su paciencia."));
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    @PostMapping("/login")
    public ResponseEntity<ResponseGeneric<DtoUser>> login(@RequestParam String email, @RequestParam String contrasenha) {
        ResponseGeneric<DtoUser> response = new ResponseGeneric<>();
        try {
            DtoUser dtoUser = businessUser.login(email, contrasenha);

            if (dtoUser == null) {
                response.setType("error");
                if (!businessUser.emailExists(email)) {
                    response.setListMessage(List.of("Usuario incorrecto"));
                } else {
                    response.setListMessage(List.of("Contraseña incorrecta"));
                }
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            response.setType("success");
            response.setListMessage(List.of("Inicio de sesión realizado correctamente"));
            response.setData(dtoUser);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Error al iniciar sesión"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseGeneric<String>> logout(@RequestParam String idUsuario) {
        ResponseGeneric<String> response = new ResponseGeneric<>();
        try {
            boolean loggedOut = businessUser.logout(idUsuario);
            if (!loggedOut) {
                response.setType("error");
                response.setListMessage(List.of("No se encontró el usuario para cerrar sesión."));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            response.setType("success");
            response.setListMessage(List.of("Cierre de sesión realizado correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Error al cerrar sesión"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{idUsuario}")
    public ResponseEntity<ResponseGeneric<DtoUser>> getUserById(@PathVariable String idUsuario) {
        ResponseGeneric<DtoUser> response = new ResponseGeneric<>();
        try {
            DtoUser dtoUser = businessUser.getUserById(idUsuario);

            if (dtoUser == null) {
                response.setType("error");
                response.setListMessage(List.of("Usuario no encontrado"));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.setType("success");
            response.setData(dtoUser);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Error al obtener el Usuario"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<ResponseGetAllUsers> getAll() {
        ResponseGetAllUsers response = new ResponseGetAllUsers();

        try {
            List<DtoUser> listDtoUser = businessUser.getAll();
            response.setData(listDtoUser);
            response.setType("success");
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Ocurrió un error inesperado."));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(path = "/update", consumes = { "multipart/form-data" })
    public ResponseEntity<ResponseUpdate> actionUpdate(@ModelAttribute RequestUpdate requestUpdate) {
        ResponseUpdate responseUpdate = new ResponseUpdate();

        try {
            DtoUser dtoUser = new DtoUser();

            dtoUser.setIdUsuario(requestUpdate.getIdUsuario());
            dtoUser.setEmail(requestUpdate.getEmail());
            dtoUser.setContrasenha(requestUpdate.getContrasenha());
            boolean updated = businessUser.update(dtoUser);

            if (!updated) {
                responseUpdate.setType("error");
                responseUpdate.setListMessage(List.of("No se encontró el registro para actualizar."));
                return new ResponseEntity<>(responseUpdate, HttpStatus.NOT_FOUND);
            }

            responseUpdate.setType("success");
            responseUpdate.setListMessage(List.of("El registro se actualizó correctamente."));
            return new ResponseEntity<>(responseUpdate, HttpStatus.OK);

        } catch (Exception e) {
            responseUpdate.setType("exception");
            responseUpdate.setListMessage(List.of("Ocurrió un error inesperado, estamos trabajando para solucionarlo."));
            return new ResponseEntity<>(responseUpdate, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{idUsuario}")
    public ResponseEntity<ResponseDelete> delete(@PathVariable String idUsuario) {
        ResponseDelete response = new ResponseDelete();
        try {
            boolean deleted = businessUser.delete(idUsuario);
            if (!deleted) {
                response.setType("error");
                response.setListMessage(List.of("No se encontró el registro para eliminar."));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            response.setType("success");
            response.setListMessage(List.of("Eliminación realizada correctamente"));
        } catch (Exception e) {
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error inesperado."));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
