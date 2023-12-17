package biscof.app.exception;

public class AlreadyExistsException extends ConflictException {
    public AlreadyExistsException(String msg) {
        super(msg);
    }
}
