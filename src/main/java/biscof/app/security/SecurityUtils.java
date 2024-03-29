package biscof.app.security;

import biscof.app.exception.exceptions.CommentNotFoundException;
import biscof.app.exception.exceptions.TaskNotFoundException;
import biscof.app.model.Comment;
import biscof.app.model.Task;
import biscof.app.repository.CommentRepository;
import biscof.app.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;

    private boolean isAuthorized(Long principalId, Long taskId, Function<Task, Long> roleExtractor) {
        Task task = taskRepository.findTaskById(taskId).orElseThrow(
                () -> new TaskNotFoundException(taskId)
        );
        Long taskUserId = roleExtractor.apply(task);
        return principalId.equals(taskUserId);
    }

    public boolean isAuthor(Long principalId, Long taskId) {
        return isAuthorized(principalId, taskId, task -> task.getAuthor().getId());
    }

    public boolean isExecutor(Long principalId, Long taskId) {
        return isAuthorized(principalId, taskId, task -> task.getExecutor().getId());
    }

    public boolean isCommentAuthor(Long principalId, Long commentId) {
        Comment comment = commentRepository.findCommentById(commentId).orElseThrow(
                () -> new CommentNotFoundException(commentId)
        );
        return principalId.equals(comment.getAuthor().getId());
    }

}
