package biscof.app.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super(String.format("No task found with ID %d.", id));
    }
}
