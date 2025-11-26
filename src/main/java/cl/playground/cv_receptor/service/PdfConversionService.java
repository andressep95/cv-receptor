package cl.playground.cv_receptor.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class PdfConversionService {

    private final FileValidationService fileValidationService;

    public PdfConversionService(FileValidationService fileValidationService) {
        this.fileValidationService = fileValidationService;
    }

    public byte[] convertToPdf(MultipartFile file) throws IOException {
        String extension = fileValidationService.getFileExtension(file.getOriginalFilename());

        return switch (extension.toLowerCase()) {
            case "pdf" -> file.getBytes();
            case "txt" -> convertTextToPdf(file);
            case "doc" -> convertDocToPdf(file);
            case "docx" -> convertDocxToPdf(file);
            default -> throw new IllegalArgumentException("Tipo de archivo no soportado: " + extension);
        };
    }

    private byte[] convertTextToPdf(MultipartFile file) throws IOException {
        String text = new String(file.getBytes(), StandardCharsets.UTF_8);
        return createPdfFromText(text);
    }

    private byte[] convertDocToPdf(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             HWPFDocument document = new HWPFDocument(is);
             WordExtractor extractor = new WordExtractor(document)) {
            String text = extractor.getText();
            return createPdfFromText(text);
        }
    }

    private byte[] convertDocxToPdf(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             XWPFDocument document = new XWPFDocument(is)) {
            StringBuilder text = new StringBuilder();
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                text.append(paragraph.getText()).append("\n");
            }
            return createPdfFromText(text.toString());
        }
    }

    private byte[] createPdfFromText(String text) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(50, 750);

                String[] lines = text.split("\n");
                float yPosition = 750;
                float leading = 14.5f;

                for (String line : lines) {
                    if (yPosition < 50) {
                        contentStream.endText();
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        PDPageContentStream newContentStream = new PDPageContentStream(document, page);
                        newContentStream.beginText();
                        newContentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                        yPosition = 750;
                        newContentStream.newLineAtOffset(50, yPosition);
                    }

                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -leading);
                    yPosition -= leading;
                }

                contentStream.endText();
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }
}