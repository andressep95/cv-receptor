package cl.playground.cv_receptor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class PresignedUrlRequest {

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("metadata")
    private Map<String, String> metadata;

    public PresignedUrlRequest() {
    }

    public PresignedUrlRequest(String filename, String contentType, Map<String, String> metadata) {
        this.filename = filename;
        this.contentType = contentType;
        this.metadata = metadata;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
