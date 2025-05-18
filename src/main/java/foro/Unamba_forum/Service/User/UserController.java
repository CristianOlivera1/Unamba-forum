package foro.Unamba_forum.Service.User;

import java.sql.Timestamp;
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
import foro.Unamba_forum.Dto.DtoRegisterUser;
import foro.Unamba_forum.Dto.DtoUser;
import foro.Unamba_forum.Dto.DtoUserProfile;
import foro.Unamba_forum.Helper.AesUtil;
import foro.Unamba_forum.Service.Generic.ResponseGeneric;
import foro.Unamba_forum.Service.User.RequestsObject.RequestUpdate;
import foro.Unamba_forum.Service.User.ResponseObject.ResponseGetAllUsers;
import foro.Unamba_forum.Service.User.ResponseObject.ResponseUpdate;

@RestController

@RequestMapping("/user")
public class UserController {

    @Autowired
    private BusinessUser businessUser;

    @PostMapping("/insert")
    public ResponseEntity<ResponseGeneric<DtoUser>> insert(@RequestParam String email,
            @RequestParam String contrasenha) {
        ResponseGeneric<DtoUser> response = new ResponseGeneric<>();
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
            response.setData(dtoUser);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List
                    .of("Ocurrió un error inesperado, estamos trabajando para resolverlo. Gracias por su paciencia."));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseGeneric<DtoUser>> login(@RequestParam String email,
            @RequestParam String contrasenha) {
        ResponseGeneric<DtoUser> response = new ResponseGeneric<>();
        try {
            DtoUser dtoUser = businessUser.login(email, contrasenha);

            if (dtoUser == null) {
                response.setType("error");
                response.setListMessage(
                        List.of(businessUser.emailExists(email) ? "Contraseña incorrecta" : "Usuario incorrecto"));
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
            response.setListMessage(List.of("Datos obtenidos correctamente."));

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
            DtoUser existingUser = businessUser.getUserById(requestUpdate.getIdUsuario());
            if (existingUser == null) {
                responseUpdate.setType("error");
                responseUpdate.setListMessage(List.of("No se encontró el registro para actualizar."));
                return new ResponseEntity<>(responseUpdate, HttpStatus.NOT_FOUND);
            }

            // Validar contraseña actual
            if (requestUpdate.getCurrentPassword() != null && !requestUpdate.getCurrentPassword().isEmpty()) {
                String encryptedCurrent = AesUtil.encrypt(requestUpdate.getCurrentPassword());
                if (!encryptedCurrent.equals(existingUser.getContrasenha())) {
                    responseUpdate.setType("error");
                    responseUpdate.setListMessage(List.of("La contraseña actual es incorrecta."));
                    return new ResponseEntity<>(responseUpdate, HttpStatus.OK);
                }
            } else {
                responseUpdate.setType("error");
                responseUpdate.setListMessage(List.of("Debes ingresar tu contraseña actual."));
                return new ResponseEntity<>(responseUpdate, HttpStatus.BAD_REQUEST);
            }

            DtoUser dtoUser = new DtoUser();
            dtoUser.setIdUsuario(requestUpdate.getIdUsuario());
            dtoUser.setContrasenha(requestUpdate.getContrasenha());
            dtoUser.setFechaRegistro(existingUser.getFechaRegistro());
            dtoUser.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
            boolean updated = businessUser.update(dtoUser);

            if (!updated) {
                responseUpdate.setType("error");
                responseUpdate.setListMessage(List.of("No se encontró el registro para actualizar."));
                return new ResponseEntity<>(responseUpdate, HttpStatus.NOT_FOUND);
            }

            responseUpdate.setType("success");
            responseUpdate.setListMessage(List.of("La contraseña se actualizó correctamente."));
            responseUpdate.setData(dtoUser);
            return new ResponseEntity<>(responseUpdate, HttpStatus.OK);

        } catch (Exception e) {
            responseUpdate.setType("exception");
            responseUpdate
                    .setListMessage(List.of("Ocurrió un error inesperado, estamos trabajando para solucionarlo."));
            return new ResponseEntity<>(responseUpdate, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/register", consumes = { "multipart/form-data" })
    public ResponseEntity<ResponseGeneric<DtoRegisterUser>> registrarUsuario(@ModelAttribute DtoRegisterUser dto) {
        ResponseGeneric<DtoRegisterUser> responseRegister = new ResponseGeneric<>();
        try {

            if (dto.getEmail().endsWith("@unamba.edu.pe")) {
                responseRegister.setType("error");
                responseRegister.setListMessage(List.of(
                        "El email no puede tener el dominio @unamba.edu.pe, si quiere registrase como usuario de la universidad, por favor, registrese con Google."));
                return new ResponseEntity<>(responseRegister, HttpStatus.OK);
            }

            if (businessUser.emailExists(dto.getEmail())) {
                responseRegister.setType("error");
                responseRegister.setListMessage(List.of("El email de usuario ya existe"));
                return new ResponseEntity<>(responseRegister, HttpStatus.OK);
            }
            businessUser.registrarUsuario(dto);
            responseRegister.setType("success");
            responseRegister.setListMessage(List.of("El registro se realizó correctamente."));
            responseRegister.setData(dto);
            return new ResponseEntity<>(responseRegister, HttpStatus.OK);
        } catch (Exception e) {
            responseRegister.setType("exception");
            responseRegister
                    .setListMessage(List.of("Ocurrió un error inesperado, estamos trabajando para solucionarlo."));
            return new ResponseEntity<>(responseRegister, HttpStatus.BAD_REQUEST);
        }
    }

    // Sugerencias de usuarios 5 por carrera y por usuarios que siguen a las
    // personas que sigo
    @GetMapping("/suggested/{idUsuario}")
    public ResponseEntity<ResponseGeneric<List<DtoUserProfile>>> getSuggestedUsers(
            @PathVariable String idUsuario, @RequestParam(defaultValue = "5") int count) {
        ResponseGeneric<List<DtoUserProfile>> response = new ResponseGeneric<>();
        try {
            List<DtoUserProfile> suggestedUsers = businessUser.getSuggestedUsers(idUsuario, count);
            response.setType("success");
            response.setData(suggestedUsers);
            response.setListMessage(List.of("Usuarios sugeridos obtenidos correctamente."));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error al obtener los usuarios sugeridos."));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{idUsuario}")
    public ResponseEntity<ResponseGeneric<String>> delete(@PathVariable String idUsuario) {
        ResponseGeneric<String> response = new ResponseGeneric<>();
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
