package cl.playground.cv_receptor.service;

import cl.playground.cv_receptor.dto.PresignedUrlRequest;
import cl.playground.cv_receptor.dto.PresignedUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PresignedUrlService {

    private final RestTemplate restTemplate;

    @Value("${presigned.url.endpoint:http://localhost:8080/api/v1/presigned-url/upload}")
    private String presignedUrlEndpoint;

    public PresignedUrlService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PresignedUrlResponse getPresignedUrl(String filename, String language, String instructions) {
        // Preparar metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("language", language);
        metadata.put("instructions", instructions);

        // Crear request
        PresignedUrlRequest request = new PresignedUrlRequest(
                filename,
                "application/pdf",
                metadata
        );

        // Configurar headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PresignedUrlRequest> entity = new HttpEntity<>(request, headers);

        // Hacer la petición POST
        ResponseEntity<PresignedUrlResponse> response = restTemplate.postForEntity(
                presignedUrlEndpoint,
                entity,
                PresignedUrlResponse.class
        );

        return response.getBody();
    }
}