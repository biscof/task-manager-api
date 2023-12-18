package biscof.app.service.comment;

import biscof.app.dto.comment.CommentDto;
import biscof.app.dto.comment.CommentResponseDto;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {

    CommentResponseDto getCommentById(Long id);

    List<CommentResponseDto> getComments(Predicate predicate, Pageable pageable);

    CommentResponseDto createComment(CommentDto commentDto);

    CommentResponseDto updateComment(Long id, CommentDto commentDto);

    void deleteComment(Long id);

}
