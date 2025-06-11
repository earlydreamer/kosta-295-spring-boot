package web.mvc;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Commit;
import web.mvc.domain.FreeBoard;
import web.mvc.domain.QFreeBoard;
import web.mvc.domain.QReply;
import web.mvc.dto.FreeBoardDTO;
import web.mvc.repository.FreeBoardRepository;

import java.util.List;
@SpringBootTest
@Transactional
public class FreeBoardJoinFetchTests {

    @Autowired
    private FreeBoardRepository freeRep;


    /**
     * FreeBoard에서 Reply의 관계는  @OneToMany이므로 FetchType.LAZY(지연로딩) 이다.
     * 먼저, FreeBoard 검색을 한 다음,
     * b.getReplyList().size() 요청으로 인해
     * 각 부모글의 글의 개수만큼 reply테이블의 select을 실행한다 - 성능이슈(많은 select요청)
     *  :해결 방인
     *     join fetch 사용
     * */
    @Test
    void join01() {
        List<FreeBoard> list=freeRep.findAll(); // FreeBoard 레코드 수 만큼 reply select (1 + N)
        System.out.println("list.size = " + list.size());
        System.out.println("댓글....");
        list.forEach(b->System.out.println(b.getBno() +" = " + b.getRepliesList().size()));
    }



}//클래스끝
