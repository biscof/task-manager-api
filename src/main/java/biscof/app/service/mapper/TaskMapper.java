package biscof.app.service.mapper;

import biscof.app.dto.TaskDto;
import biscof.app.dto.TaskResponseDto;
import biscof.app.model.Task;
import biscof.app.service.utils.UserUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class TaskMapper {

    @Autowired
    UserUtils userUtils;

    @Mapping(target = "authorName", expression = "java(userUtils.getUsersFullName(task.getAuthor()))")
    @Mapping(target = "performerName", expression = "java(userUtils.getUsersFullName(task.getPerformer()))")
    public abstract TaskResponseDto taskToTaskResponseDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", expression = "java(userUtils.getUserFromSecurityContext())")
    @Mapping(target = "performer", expression = "java(userUtils.getPerformer(taskDto.getPerformerId()))")
    public abstract Task taskDtoToTask(TaskDto taskDto);

}
