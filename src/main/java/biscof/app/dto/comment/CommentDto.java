package biscof.app.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    @NotBlank(message = "Comment title cannot be empty.")
    private String title;

    @NotBlank(message = "Comment cannot be empty.")
    private String content;

    @NotNull(message = "Task ID cannot be empty.")
    @Positive(message = "Task ID is expected to be a positive number.")
    private Long taskId;

}
