package biscof.app.service.task;

import biscof.app.dto.task.TaskDto;
import biscof.app.dto.task.TaskResponseDto;
import biscof.app.enums.Status;
import biscof.app.exception.exceptions.InvalidStatusException;
import biscof.app.exception.exceptions.TaskNotFoundException;
import biscof.app.model.Task;
import biscof.app.repository.TaskRepository;
import biscof.app.security.SecurityUtils;
import biscof.app.service.mapper.TaskMapper;
import com.querydsl.core.types.Predicate;
import biscof.app.service.utils.ServiceUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;


import java.util.List;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    TaskRepository taskRepository;
    TaskMapper taskMapper;
    @Autowired
    ServiceUtils serviceUtils;
    @Autowired
    SecurityUtils securityUtils;

    @Override
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );
        return taskMapper.taskToTaskResponseDto(task);
    }

    @Override
    public List<TaskResponseDto> getTasks(Predicate predicate, Pageable pageable) {
        List<Task> tasks = taskRepository.findAll(predicate, pageable).getContent();
        return tasks.stream()
                .map(taskMapper::taskToTaskResponseDto)
                .toList();
    }

    @Override
    public TaskResponseDto createTask(TaskDto taskDto) {
        Task task = taskMapper.taskDtoToTask(taskDto);
        taskRepository.save(task);
        return taskMapper.taskToTaskResponseDto(task);
    }

    @Override
    @PreAuthorize("@securityUtils.isAuthor(principal.id, #id)")
    public TaskResponseDto updateTask(Long id, TaskDto taskDto) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setPriority(taskDto.getPriority());
        task.setPerformer(serviceUtils.getPerformer(taskDto.getPerformerId()));

        taskRepository.save(task);
        return taskMapper.taskToTaskResponseDto(task);
    }

    @Override
    @PreAuthorize("@securityUtils.isAuthor(principal.id, #id) or hasRole('ADMIN')")
    public void deleteTask(Long id) {
        if (taskRepository.findById(id).isPresent()) {
            taskRepository.deleteById(id);
        } else {
            throw new TaskNotFoundException(id);
        }
    }

    @Override
    @PreAuthorize("@securityUtils.isAuthor(principal.id, #id) or @securityUtils.isPerformer(principal.id, #id)")
    public TaskResponseDto updateTaskStatus(Long id, String statusStr) {
        Status status;
        try {
            status = Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException();
        }
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );
        task.setStatus(status);
        taskRepository.save(task);
        return taskMapper.taskToTaskResponseDto(task);
    }

    @Override
    @PreAuthorize("@securityUtils.isAuthor(principal.id, #id)")
    public TaskResponseDto updatePerformer(Long id, Long performerId) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );
        task.setPerformer(serviceUtils.getPerformer(performerId));
        taskRepository.save(task);
        return taskMapper.taskToTaskResponseDto(task);
    }
}
