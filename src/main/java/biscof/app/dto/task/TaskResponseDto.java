package biscof.app.dto.task;

import biscof.app.enums.Priority;
import biscof.app.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {

    private Long id;

    private String title;

    private String description;

    private Status status;

    private Priority priority;

    private String authorName;

    private String performerName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss Z", timezone = "UTC")
    private Instant createdAt;

}
