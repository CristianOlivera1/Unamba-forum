package foro.Unamba_forum.Business;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Service
public class SupabaseStorageService {

        @Value("${supabase.url}")
        private String supabaseUrl;
    
        @Value("${supabase.apiKey}")
        private String supabaseApiKey;
    
        @Value("${supabase.bucket}")
        private String bucketName;
    
        public String uploadFile(MultipartFile file, String path, String contentType) {
            try {
                byte[] fileBytes = file.getBytes();
        
                // Construir la ruta completa para Supabase
                String filePath = path;
        
                // URL de subida
                String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + filePath;
        
                // Realizar la solicitud HTTP
                var client = java.net.http.HttpClient.newHttpClient();
                var request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(url))
                        .header("Authorization", "Bearer " + supabaseApiKey)
                        .header("Content-Type", contentType)  // Usar el tipo MIME correcto
                        .PUT(java.net.http.HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                        .build();
        
                var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        
                if (response.statusCode() == 200) {
                    // Retornar la URL pública
                    return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + filePath;
                } else {
                    throw new RuntimeException("Error al subir archivo a Supabase: " + response.body());
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Error al subir archivo a Supabase: " + e.getMessage());
            }
        }

        public boolean deleteFile(String filePath) {
            try {
                // Construir la URL de eliminación
                String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + filePath;
        
                // Realizar la solicitud HTTP
                var client = java.net.http.HttpClient.newHttpClient();
                var request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(url))
                        .header("Authorization", "Bearer " + supabaseApiKey)
                        .DELETE()
                        .build();
        
                var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        
                return response.statusCode() == 200;
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Error al eliminar archivo en Supabase: " + e.getMessage());
            }
        }
        
        
}