package foro.Unamba_forum.Service.Generic;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseGeneric<T> {
    private String type;
    private List<String> listMessage;
    private T data;
}
