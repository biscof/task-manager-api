package biscof.app.controller;

import biscof.app.dto.task.TaskDto;
import biscof.app.dto.task.TaskResponseDto;
import biscof.app.model.Task;
import biscof.app.service.task.TaskServiceImpl;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${base-url}/tasks")
public class TaskController {

    @Autowired
    private TaskServiceImpl taskService;

    @Operation(summary = "Get a task by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task successfully found",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = TaskResponseDto.class)) }),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = String.class)) }) }
    )
    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getTaskById(
            @Parameter(description = "ID of a task to be searched")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok().body(taskService.getTaskById(id));
    }

    @Operation(summary = "Get all tasks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks found",
                content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = TaskResponseDto.class))) }),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content) }
    )
    @GetMapping(path = "")
    public ResponseEntity<Object> getTasks(
            @Parameter(
                    description = "Filter parameters",
                    in = ParameterIn.QUERY,
                    example = "authorId=1&executorId=3&status=NEW")
            @QuerydslPredicate(root = Task.class) Predicate predicate,
            @Parameter(
                    description = "Page number, page size and sort type",
                    in = ParameterIn.QUERY,
                    example = "page=0&size=3&sort=id,desc")
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(taskService.getTasks(predicate, pageable));
    }

    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task successfully created",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = TaskResponseDto.class)) }),
        @ApiResponse(responseCode = "400", description = "Wrong data provided",
                content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = String.class))) }) }
    )
    @PostMapping(path = "")
    public ResponseEntity<Object> createTask(
            @Schema(implementation = TaskDto.class)
            @Valid @RequestBody TaskDto taskDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(taskDto));
    }

    @Operation(summary = "Update task data by task's ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task data successfully updated",
            content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = TaskResponseDto.class)) }),
        @ApiResponse(responseCode = "400", description = "Wrong data provided",
            content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content) }
    )
    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> updateTask(
            @Parameter(description = "ID of a task to be updated")
            @PathVariable Long id,
            @Schema(implementation = TaskDto.class)
            @Valid @RequestBody TaskDto taskDto
    ) {
        return ResponseEntity.ok(taskService.updateTask(id, taskDto));
    }

    @Operation(summary = "Update task status by task's ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status successfully updated",
                content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponseDto.class)) }),
        @ApiResponse(responseCode = "400", description = "Wrong data provided",
                content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content) }
    )
    @PatchMapping(path = "/{id}/status")
    public ResponseEntity<Object> updateTaskStatus(
            @Parameter(description = "ID of a task to be updated")
            @PathVariable Long id,
            @Schema(description = "Request body containing the new status")
            @RequestBody Map<String, String> requestBody
    ) {
        String newStatus = requestBody.get("status");
        return ResponseEntity.ok(taskService.updateTaskStatus(id, newStatus));
    }

    @Operation(summary = "Assign task executor by task's ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task executor successfully assigned",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = TaskResponseDto.class)) }),
        @ApiResponse(responseCode = "400", description = "Wrong data provided",
                content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content) }
    )
    @PatchMapping(path = "/{id}/executor")
    public ResponseEntity<Object> updateExecutor(
            @Parameter(description = "ID of a task to be updated")
            @PathVariable Long id,
            @Schema(description = "Request body containing the new task executor ID")
            @RequestBody Map<String, Long> requestBody
    ) {
        Long newExecutorId = requestBody.get("executorId");
        return ResponseEntity.ok(taskService.updateExecutor(id, newExecutorId));
    }

    @Operation(summary = "Delete a task by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task deleted", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found",
                content = { @Content(schema = @Schema(implementation = String.class)) }) }
    )
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteTask(
            @Parameter(description = "ID of a task to be deleted")
            @PathVariable Long id
    ) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }
}
