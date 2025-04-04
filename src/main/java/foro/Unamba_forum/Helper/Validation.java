package foro.Unamba_forum.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class Validation {

    /**
     * Normaliza el nombre de la carrera para usarlo como nombre de carpeta en
     * Supabase Storage.
     */
    public static String normalizarNombreCarrera(String nombre) {
        // Eliminar acentos y caracteres especiales
        String temp = Normalizer.normalize(nombre, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("[^\\p{ASCII}]");
        temp = pattern.matcher(temp).replaceAll("");

        // Reemplazar espacios por guiones bajos y convertir a minúsculas
        return temp.replaceAll(" ", "_").toLowerCase();
    }

    public static String normalizarNombreArchivo(String nombreArchivo) {
        return nombreArchivo.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public static byte[] descargarImagen(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        try (InputStream in = response.body()) {
            return in.readAllBytes();
        }
    }

}
