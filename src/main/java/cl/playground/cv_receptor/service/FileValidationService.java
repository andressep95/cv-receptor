package cl.playground.cv_receptor.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
public class FileValidationService {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "txt", "doc", "docx");
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "application/pdf",
            "text/plain",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    public boolean isValidFileType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return false;
        }

        String extension = getFileExtension(fileName);
        String mimeType = file.getContentType();

        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase()) &&
                (mimeType == null || ALLOWED_MIME_TYPES.contains(mimeType));
    }

    public String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public boolean isPdf(MultipartFile file) {
        String extension = getFileExtension(file.getOriginalFilename());
        return "pdf".equalsIgnoreCase(extension);
    }
}