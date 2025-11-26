package cl.playground.cv_receptor.service;

import cl.playground.cv_receptor.dto.PresignedUrlResponse;
import cl.playground.cv_receptor.dto.ProcessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ResumeProcessService {

    private static final Logger log = LoggerFactory.getLogger(ResumeProcessService.class);

    private final FileValidationService fileValidationService;
    private final PdfConversionService pdfConversionService;
    private final PresignedUrlService presignedUrlService;
    private final FileUploadService fileUploadService;

    public ResumeProcessService(FileValidationService fileValidationService,
                                PdfConversionService pdfConversionService,
                                PresignedUrlService presignedUrlService,
                                FileUploadService fileUploadService) {
        this.fileValidationService = fileValidationService;
        this.pdfConversionService = pdfConversionService;
        this.presignedUrlService = presignedUrlService;
        this.fileUploadService = fileUploadService;
    }

    public ProcessResponse processResume(MultipartFile file, String language, String instructions) {

        // Validaciones de negocio
        if (file == null || file.isEmpty()) {
            return new ProcessResponse("ERROR", "El archivo es requerido");
        }

        if (language == null || language.trim().isEmpty()) {
            return new ProcessResponse("ERROR", "El idioma es requerido");
        }

        if (instructions == null || instructions.trim().isEmpty()) {
            return new ProcessResponse("ERROR", "Las instrucciones son requeridas");
        }

        // Validar tipo de archivo
        if (!fileValidationService.isValidFileType(file)) {
            return new ProcessResponse("ERROR", "Tipo de archivo no soportado. Solo se permiten: PDF, TXT, DOC, DOCX");
        }


        try {
            // Convertir a PDF si no lo es
            byte[] pdfBytes;
            if (fileValidationService.isPdf(file)) {
                pdfBytes = file.getBytes();
            } else {
                pdfBytes = pdfConversionService.convertToPdf(file);
            }

            // Obtener nombre del archivo PDF
            String originalFileName = file.getOriginalFilename();
            String fileName = originalFileName != null ? originalFileName : "documento.pdf";

            // Cambiar extensión a .pdf si es que viene con otra extensión
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                fileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".pdf";
            }

            // Obtener presigned URL
            log.info("Solicitando presigned URL para archivo: {}", fileName);
            PresignedUrlResponse presignedUrlResponse = presignedUrlService.getPresignedUrl(
                    fileName,
                    language,
                    instructions
            );

            // Imprimir la presigned URL
            log.info("========================================");
            log.info("PRESIGNED URL OBTENIDA:");
            log.info("URL: {}", presignedUrlResponse.getUrl());
            log.info("Expira en: {}", presignedUrlResponse.getExpiresIn());
            log.info("========================================");

            // También imprimimos en consola para asegurar visibilidad
            System.out.println("========================================");
            System.out.println("PRESIGNED URL OBTENIDA:");
            System.out.println("URL: " + presignedUrlResponse.getUrl());
            System.out.println("Expira en: " + presignedUrlResponse.getExpiresIn());
            System.out.println("========================================");

            // Subir el PDF a la presigned URL con metadata
            boolean uploadSuccess = fileUploadService.uploadToPresignedUrl(
                    presignedUrlResponse.getUrl(),
                    pdfBytes,
                    language,
                    instructions
            );

            if (!uploadSuccess) {
                return new ProcessResponse("ERROR", "Error al subir el archivo a AWS S3");
            }

            return new ProcessResponse("PROCESADO", "El CV ha sido recibido, convertido a PDF y subido exitosamente a AWS S3");

        } catch (IOException e) {
            log.error("Error al procesar el archivo", e);
            return new ProcessResponse("ERROR", "Error al procesar el archivo: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error al obtener presigned URL", e);
            return new ProcessResponse("ERROR", "Error al obtener presigned URL: " + e.getMessage());
        }
    }
}
