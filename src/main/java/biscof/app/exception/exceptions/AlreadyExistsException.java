package biscof.app.exception.exceptions;

public class AlreadyExistsException extends ConflictException {
    public AlreadyExistsException(String msg) {
        super(msg);
    }
}
