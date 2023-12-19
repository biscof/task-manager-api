package biscof.app.controller;

import biscof.app.dto.user.UserDto;
import biscof.app.dto.user.UserResponseDto;
import biscof.app.service.user.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${base-url}/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Operation(summary = "Get a user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully found",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = UserResponseDto.class)) }),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content) }
    )
    @GetMapping(path = "/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @Parameter(description = "ID of a user to be searched")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Users found",
            content = { @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class))) }
    )
    @GetMapping(path = "")
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Create a new user. Sign up")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully created",
            content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = UserResponseDto.class)) }),
        @ApiResponse(responseCode = "400", description = "Wrong data provided",
            content = { @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = String.class))) }) }
    )
    @PostMapping(path = "")
    public ResponseEntity<Object> createUser(
            @Valid @RequestBody UserDto userDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
    }

    @Operation(summary = "Update user data by user's ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User data successfully updated",
            content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = UserResponseDto.class)) }),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "400", description = "Wrong data provided",
            content = { @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = String.class))) }),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content) }
    )
    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> updateUser(
            @Parameter(description = "ID of a user to be updated")
            @PathVariable Long id,
            @Valid @RequestBody UserDto userDto
    ) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @Operation(summary = "Delete a user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = { @Content(schema = @Schema(implementation = String.class)) }),
        @ApiResponse(responseCode = "409", description = "User has associated tasks",
            content = { @Content(schema = @Schema(implementation = String.class)) }) }
    )
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteUser(
            @Parameter(description = "ID of a user to be deleted")
            @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
