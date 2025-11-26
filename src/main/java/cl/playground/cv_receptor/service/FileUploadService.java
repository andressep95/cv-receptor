package cl.playground.cv_receptor.service;

import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    public boolean uploadToPresignedUrl(String presignedUrl, byte[] fileBytes, String language, String instructions) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            log.info("Uploading file to presigned URL...");
            log.info("File size: {} bytes", fileBytes.length);
            log.info("Metadata - Language: {}, Instructions: {}", language, instructions);

            // Crear PUT request
            HttpPut putRequest = new HttpPut(presignedUrl);

            // IMPORTANTE: Debemos enviar los headers de metadata porque están en SignedHeaders
            // Deben coincidir EXACTAMENTE con los valores usados al crear la presigned URL
            putRequest.setHeader("x-amz-meta-language", language);
            putRequest.setHeader("x-amz-meta-instructions", instructions);

            ByteArrayEntity entity = new ByteArrayEntity(fileBytes, org.apache.hc.core5.http.ContentType.APPLICATION_PDF);
            putRequest.setEntity(entity);

            // Ejecutar request
            try (CloseableHttpResponse response = httpClient.execute(putRequest)) {
                int statusCode = response.getCode();
                log.info("Upload response status: {}", statusCode);

                if (statusCode >= 200 && statusCode < 300) {
                    log.info("========================================");
                    log.info("ARCHIVO SUBIDO EXITOSAMENTE A AWS S3");
                    log.info("Status: {}", statusCode);
                    log.info("========================================");

                    System.out.println("========================================");
                    System.out.println("ARCHIVO SUBIDO EXITOSAMENTE A AWS S3");
                    System.out.println("Status: " + statusCode);
                    System.out.println("========================================");

                    return true;
                } else {
                    log.error("Upload failed with status: {}", statusCode);
                    return false;
                }
            }

        } catch (Exception e) {
            log.error("Error uploading file to presigned URL", e);
            return false;
        }
    }
}