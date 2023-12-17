package biscof.app.exception;

public class DeletionException extends ConflictException {
    public DeletionException(String msg) {
        super(msg);
    }
}
