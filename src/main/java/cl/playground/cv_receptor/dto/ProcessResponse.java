package cl.playground.cv_receptor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProcessResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    public ProcessResponse() {
    }

    public ProcessResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}