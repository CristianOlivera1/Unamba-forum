package foro.Unamba_forum.Business;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoNote;
import foro.Unamba_forum.Entity.TNote;
import foro.Unamba_forum.Entity.TUser;
import foro.Unamba_forum.Repository.RepoNote;
import foro.Unamba_forum.Repository.RepoUser;

@Service
public class BusinessNote {

    @Autowired
    private RepoNote repoNote;

    @Autowired
    private RepoUser repoUser;

    public DtoNote createNote(String idUsuario, String contenido, String backgroundColor, String radialGradient) {
        TUser usuario = repoUser.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        if (usuario.getPerfil() == null || usuario.getPerfil().getIdCarrera() == null) {
            throw new RuntimeException("El usuario no tiene una carrera asignada");
        }
    
        TNote note = new TNote();
        note.setIdNota(UUID.randomUUID().toString());
        note.setUsuario(usuario);
        note.setCarrera(usuario.getPerfil().getIdCarrera());
        note.setContenido(contenido);
        note.setFechaRegistro(new Timestamp(System.currentTimeMillis()));
        note.setBackgroundColor(backgroundColor);
        note.setRadialGradient(radialGradient);
    
        repoNote.save(note);
    
        return convertToDto(note);
    }

    public List<DtoNote> getNotesByUser(String idUsuario) {
        TUser usuario = repoUser.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<TNote> notes = repoNote.findByUsuario(usuario);
        return notes.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    

    public List<DtoNote> getNotesByCareer(String idCarrera) {
        List<TNote> notes = repoNote.findByCarrera(idCarrera);
        return notes.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public void deleteNoteById(String idNota) {
        TNote note = repoNote.findById(idNota)
                .orElseThrow(() -> new RuntimeException("Nota no encontrada"));
        repoNote.delete(note);
    }

    private DtoNote convertToDto(TNote note) {
        DtoNote dto = new DtoNote();
        dto.setIdNota(note.getIdNota());
        dto.setNombreCompleto(note.getUsuario().getPerfil().getNombre() + " " + note.getUsuario().getPerfil().getApellidos());
        dto.setAvatar(note.getUsuario().getPerfil().getFotoPerfil());
        dto.setContenido(note.getContenido());
        dto.setFechaPublicacion(note.getFechaRegistro());
        dto.setBackgroundColor(note.getBackgroundColor());
        dto.setRadialGradient(note.getRadialGradient());
        return dto;
    }
}
