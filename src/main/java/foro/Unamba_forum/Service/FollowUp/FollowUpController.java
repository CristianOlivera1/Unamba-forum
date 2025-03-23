package foro.Unamba_forum.Service.FollowUp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import foro.Unamba_forum.Business.BusinessFollowUp;
import foro.Unamba_forum.Dto.DtoFollowUp;
import foro.Unamba_forum.Service.FollowUp.ResponseObject.ResponseGetAllFollowUp;
import foro.Unamba_forum.Service.FollowUp.ResponseObject.ResponseInsertFollowUp;
import foro.Unamba_forum.Service.Generic.ResponseGeneric;

@RestController

@RequestMapping("/follow")
public class FollowUpController {
    
    @Autowired
    private BusinessFollowUp businessFollowUp;

    @PostMapping("/follow")
    public ResponseEntity<ResponseInsertFollowUp> followUser(@RequestParam String idSeguidor, @RequestParam String idSeguido) {
        ResponseInsertFollowUp response = new ResponseInsertFollowUp();
        try {
            businessFollowUp.followUser(idSeguidor, idSeguido);
            response.setType("success");
            response.setListMessage(List.of("Usuario seguido correctamente"));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al seguir al usuario: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<ResponseGeneric<String>> unfollowUser(@RequestParam String idSeguidor, @RequestParam String idSeguido) {
        ResponseGeneric<String> response = new ResponseGeneric<>();
        try {
            businessFollowUp.unfollowUser(idSeguidor, idSeguido);
            response.setType("success");
            response.setListMessage(List.of("Usuario dejado de seguir correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al dejar de seguir al usuario: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/followers/{idUsuario}")
    public ResponseEntity<ResponseGetAllFollowUp> getFollowers(@PathVariable String idUsuario) {
        ResponseGetAllFollowUp response = new ResponseGetAllFollowUp();
        try {
            List<DtoFollowUp> followers = businessFollowUp.getFollowers(idUsuario);
            response.setType("success");
            response.setData(followers);
            response.setListMessage(List.of("Seguidores obtenidos correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener los seguidores: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/following/{idUsuario}")
    public ResponseEntity<ResponseGetAllFollowUp> getFollowing(@PathVariable String idUsuario) {
        ResponseGetAllFollowUp response = new ResponseGetAllFollowUp();
        try {
            List<DtoFollowUp> following = businessFollowUp.getFollowing(idUsuario);
            response.setType("success");
            response.setData(following);
            response.setListMessage(List.of("Seguidos obtenidos correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener los seguidos: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/countFollowers/{idUsuario}")
    public ResponseEntity<ResponseGeneric<Long>> countFollowers(@PathVariable String idUsuario) {
        ResponseGeneric<Long> response = new ResponseGeneric<>();
        try {
            long count = businessFollowUp.countFollowers(idUsuario);
            response.setType("success");
            response.setData(count);
            response.setListMessage(List.of("Número de seguidores obtenido correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener el número de seguidores: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/countFollowing/{idUsuario}")
    public ResponseEntity<ResponseGeneric<Long>> countFollowing(@PathVariable String idUsuario) {
        ResponseGeneric<Long> response = new ResponseGeneric<>();
        try {
            long count = businessFollowUp.countFollowing(idUsuario);
            response.setType("success");
            response.setData(count);
            response.setListMessage(List.of("Número de seguidos obtenido correctamente"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al obtener el número de seguidos: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
