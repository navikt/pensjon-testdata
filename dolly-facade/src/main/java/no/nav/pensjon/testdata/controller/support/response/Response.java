package no.nav.pensjon.testdata.controller.support.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public class Response {

    private HttpStatus httpStatus;
    private String message;
    private String path;
    private Instant timestamp;

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
