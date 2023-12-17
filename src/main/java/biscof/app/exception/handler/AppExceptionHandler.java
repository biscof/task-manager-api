package biscof.app.exception.handler;

import biscof.app.exception.AuthException;
import biscof.app.exception.ConflictException;
import biscof.app.exception.ErrorResponse;
import biscof.app.exception.InvalidStatusException;
import biscof.app.exception.TaskNotFoundException;
import biscof.app.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;


@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFoundException(
            UserNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<ErrorResponse> errorList = ex.getBindingResult().getAllErrors().stream()
                .map(error -> new ErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        error.getDefaultMessage(),
                        request.getRequestURI())
                )
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorList);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(
            AuthException ex,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleDeletionException(ConflictException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStatusException(
            ConversionFailedException ex,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleCommonRuntimeException(
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
