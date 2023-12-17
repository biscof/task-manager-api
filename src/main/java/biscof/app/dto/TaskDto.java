package biscof.app.dto;

import biscof.app.enums.Priority;
import biscof.app.enums.Status;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    @NotBlank(message = "Task title must contain at least one character.")
    private String title;

    private String description;

//    @NotNull(message = "Status field can't be empty.")
//    private Long taskStatusId;

    private Status status;

    private Priority priority;

    private Long performerId;

}
