package biscof.app.exception.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(long id) {
        super(String.format("User with ID %d not found.", id));
    }

    public UserNotFoundException(String msg) {
        super(msg);
    }

}
