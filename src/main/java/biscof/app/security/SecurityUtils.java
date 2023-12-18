package biscof.app.security;

import biscof.app.exception.exceptions.TaskNotFoundException;
import biscof.app.model.Task;
import biscof.app.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final TaskRepository taskRepository;

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

    public boolean isPerformer(Long principalId, Long taskId) {
        return isAuthorized(principalId, taskId, task -> task.getPerformer().getId());
    }

}
