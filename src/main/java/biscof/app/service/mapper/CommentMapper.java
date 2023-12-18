package biscof.app.service.mapper;

import biscof.app.dto.comment.CommentDto;
import biscof.app.dto.comment.CommentResponseDto;
import biscof.app.model.Comment;
import biscof.app.service.utils.ServiceUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    @Autowired
    ServiceUtils serviceUtils;

    @Mapping(target = "authorName", expression = "java(serviceUtils.getUsersFullName(comment.getAuthor()))")
    @Mapping(target = "taskTitle", source = "comment.task.title")
    public abstract CommentResponseDto commentToResponseDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", expression = "java(serviceUtils.getUserFromSecurityContext())")
    @Mapping(target = "task", expression = "java(serviceUtils.getTask(commentDto.getTaskId()))")
    public abstract Comment dtoToComment(CommentDto commentDto);

}
