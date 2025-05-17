package foro.Unamba_forum.Helper;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BadWordsFilter {
    private static List<String> badWords;

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            badWords = mapper.readValue(
                    new ClassPathResource("badwords.json").getInputStream(),
                    List.class);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la lista de palabras inapropiadas", e);
        }
    }

    public static String censor(String text) {
        String censored = text;
        for (String word : badWords) {
            String replacement = word.charAt(0) + "*****";
            censored = censored.replaceAll("(?i)\\b" + Pattern.quote(word) + "\\b", replacement);
        }
        return censored;
    }

}
