package biscof.app.dto.comment;

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
public class CommentResponseDto {

    private Long id;

    private String title;

    private String content;

    private String authorName;

    private String taskTitle;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss Z", timezone = "UTC")
    private Instant createdAt;

}
