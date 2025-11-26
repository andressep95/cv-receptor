package cl.playground.cv_receptor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PresignedUrlResponse {

    @JsonProperty("url")
    private String url;

    @JsonProperty("expires_in")
    private String expiresIn;

    public PresignedUrlResponse() {
    }

    public PresignedUrlResponse(String url, String expiresIn) {
        this.url = url;
        this.expiresIn = expiresIn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }
}