package web.mvc.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
public class FreeBoardDTO {
	private Long bno;
	private String subject;
	private String writer;
	private String content;
	private String password;
	private int readnum; //조회수
	private LocalDateTime insertDate; //등록일

	private List<ReplyDTO> repliesList;
	
	
	

}
