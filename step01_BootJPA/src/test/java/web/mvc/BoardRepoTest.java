package web.mvc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import web.mvc.domain.Board;
import web.mvc.repository.BoardRepository;
import web.mvc.service.BoardService;

import java.util.List;

//@SpringBootTest //통합테스트용 + 기본 Commit
@DataJpaTest //단위테스트용
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//Spring Boot에서 테스트 시 사용하는 DB를 H2로 Replace하지 말고 properties에 설정된 값을 사용
@Slf4j
@Rollback(false)
public class BoardRepoTest {

    @Autowired //구현체를 따로 만들지 않았지만 SpringJPA에 의해 자동으로 구현체가 만들어줌
    private BoardRepository boardRepo;

    //지금은 단위테스트이므로 Service 영역에 대해서는 테스트하지 않는다.
    //@Autowired
    //private BoardService boardService;


    @Test
    @DisplayName("BoardRepo Test")
    @Disabled //더이상 이 메소드를 테스트에 포함시키지 않을 경우
    public void Test(){
        log.info("boardRepo : {}",boardRepo);
//        log.info("BoardService : {}",boardService);
    }

    /**
     *레코드 추가
     */
    @Test
    @DisplayName("BoardRepo Insert Test")
    @Disabled
    public void insertBoard(){
        boardRepo.save(Board.builder().title("title").content("content").build());
        System.out.println("Insert성공");

        boardRepo.save(Board.builder().id(1L).title("title").content("content222").build());
        System.out.println("수정성?공?"); //실제로는 수정이 안 됨 -> 삭제후 재생성됨 즉 영속성 유지안됨
        // 왜? 새 객체를 만들어 수정하는 값만 덮어씌웠기 때문... 비영속 객체이기 때문에 JPA를 통한 UPDATE가 정상동작하지 않는다.
        // 입력 안된 부분은 null로 덮어 씌워진다.
        // UpdateDate는 왜 갱신됐지??? -> 자동으로 들어감? -> annotation이 붙어있다.


        for(int i=2;i<=200;i++){
            boardRepo.save(
                Board.builder().title("제목 "+i).content("content "+i).build()
            );
        }


    }
    /**
     * 전체 검색
     */

    @Test
    @DisplayName("BoardRepo Select All Test")
    @Disabled
    public void selectBoard(){
        List<Board> list = boardRepo.findAll();
        list.forEach(System.out::println);

    }
    /**
     * PK 조건으로 검색
     */
    @Test
    @DisplayName("BoardRepo SelectById Test")
    public void selectBoardById(){
        //boardRepo.findById(999L).ifPresent(System.out::println);
        Board board = boardRepo.findById(1L).orElse(null);
        System.out.println(board);
        //update 테스트
        if(board!=null){
            System.out.println(board);
            board.setContent("ㅇㅅㅇ");
            boardRepo.save(board);

        }
    }

    /**
     * 삭제 테스트
     */
    @Test
    @DisplayName("BoardRepo Delete Test")
    public void deleteBoard(){
        boardRepo.deleteById(2L);

    }


    /**
     * 페이징 테스트
     */

    @Test
    @DisplayName("페이징 테스트")
    public void SelectPageTest(){
        Pageable pageable =
                PageRequest.of(0,10, Sort.Direction.ASC,"id");

        Page<Board> page = boardRepo.findAll(pageable);

        System.out.println("-----------------------");
        System.out.println("page.getNumber() = " + page.getNumber());
        System.out.println("page.getSize() = " + page.getSize());
        System.out.println("page.getTotalPages() = " + page.getTotalPages());
        System.out.println("page.previousPageable() = " + page.previousPageable());
        System.out.println("page.nextPageable() = " + page.nextPageable());

        System.out.println("page.isFirst() = " + page.isFirst());
        System.out.println("page.isLast() = " + page.isLast());
        System.out.println("page.hasNext() = " + page.hasNext());
        System.out.println("page.hasPrevious() = " + page.hasPrevious());

        System.out.println("****************************************");
        List<Board> list = page.getContent();
        list.forEach(board -> System.out.println(board));


        System.out.println("----------------------------");
    }




}
