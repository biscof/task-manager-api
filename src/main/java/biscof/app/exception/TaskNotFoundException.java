package biscof.app.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("No task found with ID " + id + ".");
    }
}
