package biscof.app.controller;

import biscof.app.dto.comment.CommentDto;
import biscof.app.model.Comment;
import biscof.app.service.comment.CommentServiceImpl;
import com.querydsl.core.types.Predicate;
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

    //    @Operation(summary = "Get a task by ID")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Task successfully found",
//                content = { @Content(mediaType = "application/json",
//                        schema = @Schema(implementation = Task.class)) }),
//        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
//        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
//        @ApiResponse(responseCode = "404", description = "Task not found",
//                content = { @Content(mediaType = "application/json",
//                        schema = @Schema(implementation = String.class)) }),
//        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getCommentById(
//            @Parameter(description = "ID of a task to be searched")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok().body(commentService.getCommentById(id));
    }

    //    @Operation(summary = "Get all tasks")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Tasks found",
//                content = { @Content(mediaType = "application/json",
//                        array = @ArraySchema(schema = @Schema(implementation = Task.class))) }),
//        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
//        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
//        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    @GetMapping(path = "")
    public ResponseEntity<Object> getComments(
//            @Parameter(hidden = true)
            @QuerydslPredicate(root = Comment.class) Predicate predicate,
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getComments(predicate, pageable));
    }

    //    @Operation(summary = "Create a new task")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "201", description = "Task successfully created",
//                content = { @Content(mediaType = "application/json",
//                        schema = @Schema(implementation = Task.class)) }),
//        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
//        @ApiResponse(responseCode = "422", description = "Wrong data provided",
//                content = { @Content(mediaType = "application/json",
//                        array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
//        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    @PostMapping(path = "")
    public ResponseEntity<Object> createComment(
            @Valid @RequestBody CommentDto commentDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(commentDto));
    }

    //    @Operation(summary = "Update task data by task's ID")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Task data successfully updated",
//            content = { @Content(mediaType = "application/json",
//                        schema = @Schema(implementation = Task.class)) }),
//        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
//        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
//        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
//        @ApiResponse(responseCode = "422", description = "Wrong data provided",
//            content = { @Content(mediaType = "application/json",
//                        array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
//        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> updateComment(
//            @Parameter(description = "ID of a task to be updated")
            @PathVariable Long id,
            @Valid @RequestBody CommentDto commentDto
    ) {
        return ResponseEntity.ok(commentService.updateComment(id, commentDto));
    }

    //    @Operation(summary = "Delete a task by its ID")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Task deleted", content = @Content),
//        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
//        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
//        @ApiResponse(responseCode = "404", description = "Task not found",
//                content = { @Content(schema = @Schema(implementation = String.class)) }),
//        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
//    })

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteComment(
//            @Parameter(description = "ID of a task to be deleted")
            @PathVariable Long id
    ) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}

