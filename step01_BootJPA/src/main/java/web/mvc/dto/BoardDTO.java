package web.mvc.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BoardDTO {
    private Long bno;
    private String title;
    private String author;
    private String content;
    private LocalDateTime insertDate;
    private LocalDateTime updateDate;
}
