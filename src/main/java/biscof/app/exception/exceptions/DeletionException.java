package biscof.app.exception.exceptions;

public class DeletionException extends ConflictException {
    public DeletionException(String msg) {
        super(msg);
    }
}
