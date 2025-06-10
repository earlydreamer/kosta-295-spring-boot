package web.mvc;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.domain.Board;
import web.mvc.domain.QBoard;
import web.mvc.dto.BoardDTO;
import web.mvc.repository.BoardRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jdk.javadoc.internal.tool.Main.execute;

@SpringBootTest //통합테스트용 + 기본 Commit
@Slf4j
public class QueryDslTest {

    @Autowired
    private JPAQueryFactory queryFactory;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Test
    public void init(){
        log.info("jpaqueryfactory:{}", queryFactory);
        log.info("boardRepository:{}", boardRepository);
    }


    @Test
    public void queryDslTest01(){
        QBoard board = QBoard.board;
        List<Board> list = queryFactory.selectFrom(board)
                .fetch();

        list.forEach(System.out::println);


    }

    @Test
    public void queryDslTest02(){
        QBoard board = QBoard.board;
        List<Board> list = queryFactory.selectFrom(board)
                .where(board.id.lt(30L)
                        .or(board.title.eq("title80")))
                .fetch();

        list.forEach(System.out::println);


    }


    @Test
    @Transactional
    @Rollback(false)
    public void queryDslTest03(){
        QBoard board = QBoard.board;
        long res = queryFactory.delete(board).where(board.id.lt(10)).execute();
        log.info("res:{}", res);



    }

    @Test
    @Transactional
    @Rollback(false)
    public void queryDslTest04(){
        QBoard board = QBoard.board;
        long res = queryFactory
                .update(board).set(board.title,"asdf").set(board.content,"content...12asdf3")
                .set(board.updateDate, LocalDateTime.now())
                .where(board.id.eq(1L))
                .execute();

        //직접쿼리실행이므로 UpdateTimeStamp가 동작안한다
        //직접넣어주면 동작하기는 한다. 다만 이 동작은 영속성과는 무관하다

        log.info("res:{}", res);


    }

    @Test
    /**
     * interface에 QueryPredicateExecutor<> 상속받는다.
     *   - QueryPredicateExecutor안에서 제공하는 메소드를 사용해서 자바중심으로 조건을 만들수 있다.
     *   -Spring Data JPA + QueryDSL을 접목한 형태로 Repository에서 바로 QueryDSL의
     *   `Predicate`를 실행할 수 있도록 지원한다.
     *   - JPAQueryFactory 없이 간단하게 Predicate로 해결
     *   ex)  ~.findAll(Predicate p)
     *
     *   참고 : https://www.notion.so/QuerydslPredicateExecutor-T-208a7a6c42ce80b290bde247b33a49ef?pvs=12
     * */
    public void queryDslTest05(){
        QBoard board = QBoard.board;
        BooleanBuilder builder = new BooleanBuilder();
//        builder.and(board.id.eq(9L));
//        builder.or(board.title.like("%user%"));

    Iterable<Board> it = boardRepository.findAll(builder);

        List<Board> list = Lists.newArrayList(it);



    }

    /// ///////QueryPredicateExecutor 사용하기///////////////////////////////////////////
    /**
     * interface에 QueryPredicateExecutor<> 상속받는다.
     *   - QueryPredicateExecutor안에서 제공하는 메소드를 사용해서 자바중심으로 조건을 만들수 있다.
     *   -Spring Data JPA + QueryDSL**을 접목한 형태로 Repository에서 바로 QueryDSL의
     *   `Predicate`를 실행할 수 있도록 지원한다.
     *   - JPAQueryFactory 없이 간단하게 Predicate로 해결
     *   ex)  ~.findAll(Predicate p)
     *
     *   참고 : https://www.notion.so/QuerydslPredicateExecutor-T-208a7a6c42ce80b290bde247b33a49ef?pvs=12
     * */
    @Test
    public void queryDSL05(){
        BooleanBuilder builder = new BooleanBuilder();// 조건을 만들 때 사용 한 것 (where)
        QBoard board = QBoard.board;
        //1)
        // builder.and(board.bno.eq(2L)); //where bno=2
        // builder.or(board.author.like("%User1%")) ;// where author like '%User1%'

        //2) insert_date between ? and ?
        LocalDateTime from = LocalDateTime.of(2025, 6, 1,0,0,0);
        LocalDateTime to = LocalDateTime.of( 2025, 6, 5,12,0,0);
        builder.and(board.insertDate.between(from, to)); //insert_date between ? and ?

        //3)
        //builder.and(board.author.eq("user1")); //대소문자구분한다.

        //4)
        //builder.and(board.author.equalsIgnoreCase("user9"));  //대소문자 구분안한다.

        //5)
        //builder.and(board.author.toUpperCase().eq("user9".toUpperCase()));

        //6)
//        builder.and(board.writer.toUpperCase().eq("user9".toUpperCase())).or(board.id.gt(140L));


        Iterable<Board> iterable = boardRepository.findAll(builder);//where b1_0.bno=? or b1_0.writer like ?
       /*   Iterator<Board> it =  iterable.iterator();
          while(it.hasNext()){
              Board b = it.next();
              System.out.println(b);
          }*/

        //Iterable를  ArrayList로 변환 해준다.
        List<Board> list = Lists.newArrayList(iterable);
        list.forEach(board1 -> System.out.println(board1));

    }

    //////////////////////////////////
    /**
     * 제목에 키워드가 포함되고, 작성자가 특정인인 게시글 조회 - BooleanBuilder이용
     * */
    @Test
    public void queryDSL06(){
        String titleKeyword = "제목10";
        String authorName = null;
        QBoard board = QBoard.board;

        //BooleanBuilder는 여러 조건을 동적으로 조합할 때 매우 유용
        BooleanBuilder builder = new BooleanBuilder();

        if (titleKeyword != null && !titleKeyword.isEmpty()) {
            builder.and(board.title.containsIgnoreCase(titleKeyword));
        }

        if (authorName != null && !authorName.isEmpty()) {
            builder.and(board.writer.eq(authorName));
        }

        Iterable<Board> it = boardRepository.findAll(builder);
    }

    /**
     * 제목에 키워드가 포함되고, 작성자가 특정인인 게시글 조회 -
     *  jpaQueryFactory의 where절에 직접 조건식 사용
     * */
    @Test
    public void queryDSL07(){
        String titleKeyword = "제목10";
        String authorName = null;

        QBoard board = QBoard.board;

        List<Board> result = jpaQueryFactory
                .selectFrom(board)
                .where(
                        titleKeyword != null && !titleKeyword.isEmpty() ? board.title.containsIgnoreCase(titleKeyword) : null,
                        authorName != null && !authorName.isEmpty() ? board.writer.eq(authorName) : null
                )
                .fetch();
    }
    /**
     * 제목에 키워드가 포함되고, 작성자가 특정인인 게시글 조회 - List<BooleanExpression>
     * */
    @Test
    public void queryDSL08(){
        String titleKeyword = "제목10";
        String authorName = null;

        QBoard board = QBoard.board;

        // 동적 조건 조립 (null 체크 후 where절에 넣기 위해)
        List<BooleanExpression> conditions = new ArrayList<>();

        if (titleKeyword != null && !titleKeyword.isEmpty()) {
            conditions.add(board.title.containsIgnoreCase(titleKeyword));
        }

        if (authorName != null && !authorName.isEmpty()) {
            conditions.add(board.writer.eq(authorName));
        }

// 조건 배열을 가변 인자로 넘김
        List<Board> result = jpaQueryFactory
                .selectFrom(board)
                .where(conditions.toArray(new BooleanExpression[0]))
                .fetch();
    }

    /**
     * QueryDSL의 Projections
     *  : QueryDSL의 Projections는 쿼리 결과를 DTO에 매핑할 때 사용하는 도구
     * */
    @Test
    public void translateDTO(){
        QBoard board = QBoard.board;
        List<BoardDTO>  list =
                jpaQueryFactory
                        .select(
                                Projections.fields(
                                        BoardDTO.class,
                                        board.id, board.title,
                                        board.writer,
                                        board.content,
                                        board.insertDate,
                                        board.updateDate)
                        )
                        .from(board)
                        .fetch();

        list.forEach(b->System.out.println(b));
    }

}