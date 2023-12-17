package biscof.app.service.task;

import biscof.app.dto.TaskDto;
import biscof.app.dto.TaskResponseDto;
import biscof.app.enums.Status;
import biscof.app.exception.InvalidStatusException;
import biscof.app.exception.TaskNotFoundException;
import biscof.app.model.Task;
import biscof.app.repository.TaskRepository;
import biscof.app.service.mapper.TaskMapper;
import biscof.app.utils.UserUtils;
//import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    TaskRepository taskRepository;
    TaskMapper taskMapper;
    @Autowired
    UserUtils userUtils;

    @Override
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );
        return taskMapper.taskToTaskResponseDto(task);
    }

//    @Override
//    public List<TaskResponseDto> getTasks(Predicate predicate) {
//        List<Task> tasks;
//        if (predicate != null) {
//            tasks = (List<Task>) taskRepository.findAll(predicate);
//        } else {
//            tasks = taskRepository.findAll();
//        }
//        return tasks.stream()
//                .map(taskMapper::taskToTaskResponseDto)
//                .toList();
//    }

    @Override
    public TaskResponseDto createTask(TaskDto taskDto) {
        Task task = taskMapper.taskDtoToTask(taskDto);
        taskRepository.save(task);
        return taskMapper.taskToTaskResponseDto(task);
    }

    @Override
    public TaskResponseDto updateTask(Long id, TaskDto taskDto) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setPriority(taskDto.getPriority());
        task.setPerformer(userUtils.getPerformer(taskDto.getPerformerId()));

        taskRepository.save(task);
        return taskMapper.taskToTaskResponseDto(task);
    }

    @Override
    public void deleteTask(Long id) {
        if (taskRepository.findById(id).isPresent()) {
            taskRepository.deleteById(id);
        } else {
            throw new TaskNotFoundException(id);
        }
    }

    @Override
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
    public TaskResponseDto updatePerformer(Long id, Long performerId) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );
        task.setPerformer(userUtils.getPerformer(performerId));
        taskRepository.save(task);
        return taskMapper.taskToTaskResponseDto(task);
    }
}
