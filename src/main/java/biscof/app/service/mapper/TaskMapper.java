package biscof.app.service.mapper;

import biscof.app.dto.task.TaskDto;
import biscof.app.dto.task.TaskResponseDto;
import biscof.app.model.Task;
import biscof.app.service.utils.ServiceUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@Mapper(componentModel = "spring")
public abstract class TaskMapper {

    @Autowired
    ServiceUtils serviceUtils;

    @Mapping(target = "authorName", expression = "java(serviceUtils.getUsersFullName(task.getAuthor()))")
    @Mapping(target = "executorName", expression = "java(serviceUtils.getUsersFullName(task.getExecutor()))")
    public abstract TaskResponseDto taskToTaskResponseDto(Task task);

    public Task taskDtoToTask(TaskDto taskDto) {
        return Task.builder()
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .status(taskDto.getStatus())
                .priority(taskDto.getPriority())
                .author(serviceUtils.getUserFromSecurityContext())
                .executor(serviceUtils.getExecutor(taskDto.getExecutorId()))
                .comments(new ArrayList<>())
                .build();
    };

}
