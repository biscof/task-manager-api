package biscof.app.controller;

import biscof.app.dto.comment.CommentDto;
import biscof.app.dto.comment.CommentResponseDto;
import biscof.app.model.Comment;
import biscof.app.service.comment.CommentServiceImpl;
import com.querydsl.core.types.Predicate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${base-url}/comments")
public class CommentController {

    @Autowired
    private CommentServiceImpl commentService;

    @Operation(summary = "Get a comment by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment successfully found",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = CommentResponseDto.class)) }),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "Comment not found",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = String.class)) }) }
    )
    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getCommentById(
            @Parameter(description = "ID of a comment to be searched")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok().body(commentService.getCommentById(id));
    }

    @Operation(summary = "Get all comments (paginated and filtered)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comments found",
                content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = CommentResponseDto.class))) }),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content) }
    )
    @GetMapping(path = "")
    public ResponseEntity<Object> getComments(
            @Parameter(
                    description = "Filter parameters",
                    in = ParameterIn.QUERY,
                    example = "authorId=1&taskId=8")
            @QuerydslPredicate(root = Comment.class) Predicate predicate,
            @Parameter(
                    description = "Page number, page size and sort type",
                    in = ParameterIn.QUERY,
                    example = "page=0&size=3&sort=id,desc")
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getComments(predicate, pageable));
    }

    @Operation(summary = "Create a new comment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Comment successfully created",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = CommentResponseDto.class)) }),
        @ApiResponse(responseCode = "400", description = "Wrong data provided",
                content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content) }
    )
    @PostMapping(path = "")
    public ResponseEntity<Object> createComment(
            @Valid @RequestBody CommentDto commentDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(commentDto));
    }

    @Operation(summary = "Update comment data by comment's ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment successfully updated",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = CommentResponseDto.class)) }),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "400", description = "Wrong data provided",
                content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
        @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content) }
    )
    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> updateComment(
            @Parameter(description = "ID of a comment to be updated")
            @PathVariable Long id,
            @Valid @RequestBody CommentDto commentDto
    ) {
        return ResponseEntity.ok(commentService.updateComment(id, commentDto));
    }

    @Operation(summary = "Delete a comment by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment deleted", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "Comment not found",
                content = { @Content(schema = @Schema(implementation = String.class)) }) }
    )
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteComment(
            @Parameter(description = "ID of a comment to be deleted")
            @PathVariable Long id
    ) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}

