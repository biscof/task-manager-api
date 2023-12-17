package biscof.app.dto;

import biscof.app.enums.Priority;
import biscof.app.enums.Status;
import biscof.app.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {

    private String title;

    private String description;

    private Status status;

    private Priority priority;

    private String authorName;

    private String performerName;

}
