package web.mvc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.test.annotation.Rollback;
import web.mvc.domain.Board;
import web.mvc.repository.BoardRepository;

import java.util.List;

//@SpringBootTest //통합테스트용 + 기본 Commit
@DataJpaTest //단위테스트용
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//Spring Boot에서 테스트 시 사용하는 DB를 H2로 Replace하지 말고 properties에 설정된 값을 사용
@Slf4j
@Rollback(false)
public class JPQLTest {

    @Autowired //구현체를 따로 만들지 않았지만 SpringJPA에 의해 자동으로 구현체가 만들어줌
    private BoardRepository boardRepo;

    //지금은 단위테스트이므로 Service 영역에 대해서는 테스트하지 않는다.
    //@Autowired
    //private BoardService boardService;

    /**
     *Select 테스트
     */

    @Test
    @DisplayName("BoardRepo Select Test")
    public void select(){
        boardRepo.findByNoOrTitleTest(9L, "title")
                .forEach(board -> {System.out.println(board);
                });

    }

    @Test
    @DisplayName("글번호or제목or작성자")
    public void selectByWhere(){
        List<Board> list = boardRepo.findByWhere(Board.builder().title("제목1").id(40L).writer("user").build());
        list.forEach( board ->{
            System.out.println(board);
        });
    }


    //--

    @Test
    @DisplayName("Query Method 테스트 > 5보다 낮은 ID 혹은 작성자가 writer인 항목을 Board에서 찾는 쿼리")
    void findByIdLessThanAndWriter(){
        List<Board> result = boardRepo.findByIdLessThanOrWriter(5L, "writer");
        result.forEach(System.out::println);
    }




}