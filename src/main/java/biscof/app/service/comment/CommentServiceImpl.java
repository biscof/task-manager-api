package biscof.app.service.comment;

import biscof.app.dto.comment.CommentDto;
import biscof.app.dto.comment.CommentResponseDto;
import biscof.app.exception.exceptions.CommentNotFoundException;
import biscof.app.model.Comment;
import biscof.app.repository.CommentRepository;
import biscof.app.security.SecurityUtils;
import biscof.app.service.mapper.CommentMapper;
import biscof.app.service.utils.ServiceUtils;
import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;
    CommentMapper commentMapper;
    @Autowired
    ServiceUtils serviceUtils;
    @Autowired
    SecurityUtils securityUtils;

    @Override
    public CommentResponseDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new CommentNotFoundException(id)
        );
        return commentMapper.commentToResponseDto(comment);
    }

    @Override
    public List<CommentResponseDto> getComments(Predicate predicate, Pageable pageable) {
        List<Comment> comments = commentRepository.findAll(predicate, pageable).getContent();
        return comments.stream()
                .map(commentMapper::commentToResponseDto)
                .toList();
    }

    @Override
    public CommentResponseDto createComment(CommentDto commentDto) {
        Comment comment = commentMapper.dtoToComment(commentDto);
        commentRepository.save(comment);
        return commentMapper.commentToResponseDto(comment);
    }

    @Override
    @PreAuthorize("@securityUtils.isCommentAuthor(principal.id, #id)")
    public CommentResponseDto updateComment(Long id, CommentDto commentDto) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new CommentNotFoundException(id)
        );
        comment.setTitle(commentDto.getTitle());
        comment.setContent(commentDto.getContent());

        commentRepository.save(comment);
        return commentMapper.commentToResponseDto(comment);
    }

    @Override
    @PreAuthorize("@securityUtils.isCommentAuthor(principal.id, #id) or hasRole('ADMIN')")
    public void deleteComment(Long id) {
        if (commentRepository.findById(id).isPresent()) {
            commentRepository.deleteById(id);
        } else {
            throw new CommentNotFoundException(id);
        }
    }

}
