package foro.Unamba_forum.Dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoReactionAndCommentSummary {
    private List<DtoReactionSummary> reacciones;
    private long totalComentarios;
}
