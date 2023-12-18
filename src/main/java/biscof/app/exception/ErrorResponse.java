package biscof.app.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
@Setter
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss Z", timezone = "UTC")
    private Instant timestamp;

    private int code;

    private String status;

    private String message;

    private String path;

    public ErrorResponse() {
        timestamp = Instant.now();
    }

    public ErrorResponse(HttpStatus httpStatus, String message, String path) {
        this();
        this.code = httpStatus.value();
        this.status = httpStatus.name();
        this.message = message;
        this.path = path;
    }

}
