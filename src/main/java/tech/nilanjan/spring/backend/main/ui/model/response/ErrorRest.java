package tech.nilanjan.spring.backend.main.ui.model.response;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.Date;

public class ErrorRest {
    private final String errorMessage;
    private final HttpStatus httpStatus;
    private final Boolean success = false;
    private final ZonedDateTime timestamp;

    public ErrorRest(
            String errorMessage,
            HttpStatus httpStatus,
            ZonedDateTime timestamp
    ) {
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Boolean getSuccess() {
        return success;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}
