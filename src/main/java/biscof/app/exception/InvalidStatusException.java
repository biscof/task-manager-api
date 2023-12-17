package biscof.app.exception;

public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException() {
        super("Provided status invalid. Acceptable values are: new, pending, in_progress, completed");
    }
}
