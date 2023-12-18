package biscof.app.exception.exceptions;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(Long id) {
        super(String.format("No comment found with ID %d.", id));
    }


}
