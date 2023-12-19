package biscof.app.service.task;

import biscof.app.dto.task.TaskDto;
import biscof.app.dto.task.TaskResponseDto;

import java.util.List;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;


public interface TaskService {

    TaskResponseDto getTaskById(Long id);

    List<TaskResponseDto> getTasks(Predicate predicate, Pageable pageable);

    TaskResponseDto createTask(TaskDto taskDto);

    TaskResponseDto updateTask(Long id, TaskDto taskDto);

    void deleteTask(Long id);

    TaskResponseDto updateTaskStatus(Long id, String status);

    TaskResponseDto updateExecutor(Long id, Long executorId);
}
