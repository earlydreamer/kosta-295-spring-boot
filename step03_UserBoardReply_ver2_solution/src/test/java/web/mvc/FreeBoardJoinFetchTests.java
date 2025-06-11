package web.mvc;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
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

    @Autowired
    private JPAQueryFactory jpaFactory;

    /**
     * FreeBoard에서 Reply의 관계는  @OneToMany이므로 FetchType.LAZY(지연로딩) 이다.
     * 먼저, FreeBoard 검색을 한 다음,
     * b.getReplyList().size() 요청으로 인해
     * 각 부모글의 글의 개수만큼 reply테이블의 select을 실행한다 - 성능이슈(많은 select요청)
     *  :해결 방인
     *     join fetch 사용
     * */
    @Test
    @DisplayName("findAll()사용")
    void join01() {
        List<FreeBoard> list=freeRep.findAll(); // FreeBoard 레코드 수 만큼 reply select (1 + N)
        System.out.println("list.size = " + list.size());
        System.out.println("댓글....");
        list.forEach(b->System.out.println(b.getBno() +" = " + b.getRepliesList().size()));
    }

    /**
     * JPQL을 이용해서 join을 해본다.
     *   @Query("select f from FreeBoard f left join  f.repliesList")
     *   List<FreeBoard> join02();
     *
     *  결과는 join01() 단위 테스트와 동일하다(성능이슈)
     * */
    @Test
    @DisplayName("JPQL이용 Join")
    void join02() {
        List<FreeBoard> list=freeRep.join02(); // FreeBoard 레코드 수 만큼 reply select (1 + N)
        System.out.println("list.size = " + list.size());
        System.out.println("댓글....");
        list.forEach(b->System.out.println(b.getBno() +" = " + b.getRepliesList().size()));//댓글 조회한다.

    }

    /**
     * JPQL join fetch 를 해본다.
     *    @Query("select f from FreeBoard f left join fetch f.repliesList")
     *     List<FreeBoard> join03();
     *
     * */
    @Test
    @DisplayName("join fetch 사용")
    void join03() {
        List<FreeBoard> list = freeRep.join03();
        System.out.println("list.size = " + list.size());
        System.out.println("----------------------------");
        // list.forEach(b->System.out.println(b.getBno() +" = " + b.getRepliesList().size()));

        list.forEach(b->{
            System.out.println(b.getBno()+" | " + b.getSubject());
            b.getRepliesList().forEach(r->{
                System.out.println("====> " +r.getRno()+" | " +r.getContent()+" | "+ r.getRno());
            });
            System.out.println();
        });
    }

    /**
     * JPQL join fetch에서 페이징 처리 해보자
     * */
    @Test
    @DisplayName("join fetch,Page리턴 사용")
    void join04() {
        Pageable pageable =
                PageRequest.of(1,5 , Sort.Direction.DESC , "bno");
        //countQuery적용
        Page<FreeBoard> page = freeRep.join04(pageable);

        System.out.println("***************************");
        System.out.println("page.getNumber() = "+page.getNumber());
        System.out.println("page.getSize() = "+page.getSize());
        System.out.println("page.getTotalPages() = "+page.getTotalPages());
        System.out.println("page.previousPageable() = "+page.previousPageable());
        System.out.println("page.nextPageable() = "+page.nextPageable());

        System.out.println("page.isFirst() = "+page.isFirst());
        System.out.println("page.isLast() = "+page.isLast());

        System.out.println("page.hasPrevious() = "+page.hasPrevious());
        System.out.println("page.hasNext() = "+page.hasNext());
        System.out.println("*****************************************");

        System.out.println("list.size = " + page.getContent().size());
        //list.forEach(b->System.out.println(b.getBno() +" = " + b.getReplyList().size()));

        page.getContent().forEach(b->{
            System.out.println(b.getBno()+" | " + b.getSubject());
            b.getRepliesList().forEach(r->{
                System.out.println("====> " +r.getRno()+" | " +r.getContent()+" | "+ r.getRno());
            });
            System.out.println();
        });
    }


    //join04에는 버그가 있다.
    //페이징 처리가 정상적으로 되지 않는 문제.
    //reply에 대한 중복처리가 필요하다. (join 결과를 기준으로 count를 가져오므로 동일 게시글에 대한 댓글이 함께 count에 집계된다)
    //중복에 대한 대응이 필요하다. (count 처리 필요)


    ////////페이징처리 추가/////////////////////////////////////
    /**
     * JPQL문법을 이용하여
     * join fetch + 페이징처리 쿼리
     *     @Query(value = "select distinct f from FreeBoard f  left join fetch f.repliesList",
     *     countQuery = "select count(distinct f.bno) from FreeBoard f left join f.repliesList" )
     *     Page<FreeBoard> join05(Pageable page);
     * */
    @Test
    @DisplayName("join fetch + countQuery")
    void join05() {
        Pageable pageable =
                PageRequest.of(1,5 , Sort.Direction.DESC , "bno");
        //countQuery적용
        Page<FreeBoard> page = freeRep.join05(pageable);

        System.out.println("***************************");
        System.out.println("page.getNumber() = "+page.getNumber());
        System.out.println("page.getSize() = "+page.getSize());
        System.out.println("page.getTotalPages() = "+page.getTotalPages());
        System.out.println("page.previousPageable() = "+page.previousPageable());
        System.out.println("page.nextPageable() = "+page.nextPageable());

        System.out.println("page.isFirst() = "+page.isFirst());
        System.out.println("page.isLast() = "+page.isLast());

        System.out.println("page.hasPrevious() = "+page.hasPrevious());
        System.out.println("page.hasNext() = "+page.hasNext());
        System.out.println("*****************************************");

        System.out.println("list.size = " + page.getContent().size());
        //list.forEach(b->System.out.println(b.getBno() +" = " + b.getReplyList().size()));

        page.getContent().forEach(b->{
            System.out.println(b.getBno()+" | " + b.getSubject());
            b.getRepliesList().forEach(r->{
                System.out.println("====> " +r.getRno()+" | " +r.getContent()+" | "+ r.getRno());
            });
            System.out.println();
        });
    }
    ///////QueryDSL 사용하기 /////////////////////////////////
    /**
     * 준비사항
     * 1. Repository 에 QuerydslPredicateExecutor<FreeBoard> 상속
     * 2. @Configuration 클래스에 JPAQueryFactory bean으로 등록
     * */
    /**
     * QueryDSL를 이용한 join fetch
     * */
    @Test
    @DisplayName("QueryDSL join fetch")
    public void queryDSL01(){
        QFreeBoard freeBoard = QFreeBoard.freeBoard;
        QReply reply = QReply.reply;

        List<FreeBoard> list = jpaFactory
                               .selectFrom(freeBoard)
                               .leftJoin(freeBoard.repliesList).fetchJoin()
                               .fetch();

        System.out.println("list.size = " + list.size());
        System.out.println("----------------------------");
        // list.forEach(b->System.out.println(b.getBno() +" = " + b.getRepliesList().size()));

        list.forEach(b->{
            System.out.println(b.getBno()+" | " + b.getSubject());
            b.getRepliesList().forEach(r->{
                System.out.println("====> " +r.getRno()+" | " +r.getContent()+" | "+ r.getRno());
            });
            System.out.println();
        });
    }


    /**
     * QueryDSL를 이용한 join fetch + 페이징처리
     * */
    @Test
    public void queryDSL02() {

        /*
        * @Query(value = "select distinct f from FreeBoard f  left join fetch f.repliesList",
         *     countQuery = "select count(distinct f.bno) from FreeBoard f left join f.repliesList" )

        *    //정렬기준 2개이상 일때
       *    Pageable pageable = PageRequest.of(
                0, // 페이지 번호 (0부터 시작)
                5, // 페이지 크기
                Sort.by(
                    Sort.Order.asc("bno"),
                    Sort.Order.desc("title")
                )
            );
       *
         * */

        //여기서 "bno"는 FreeBoard 엔티티의 필드
        Pageable pageable = PageRequest.of(0,5 , Sort.Direction.DESC , "bno");

        QFreeBoard freeBoard = QFreeBoard.freeBoard;
        QReply reply = QReply.reply;

        // 1. 본문 데이터 쿼리 (fetch join)
        List<FreeBoard> content = jpaFactory
                .selectFrom(freeBoard)
                .distinct()
                .leftJoin(freeBoard.repliesList, reply).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2. Count 쿼리 (fetch join 없이)
        long totalCount = jpaFactory
                .select(freeBoard.bno.countDistinct())
                .from(freeBoard)
                .leftJoin(freeBoard.repliesList, reply)
                .fetchOne();

        System.out.println("totalCount = " + totalCount);

        // 3. Page 객체로 반환
        Page<FreeBoard> page = new PageImpl<>(content, pageable, totalCount);

        System.out.println("***************************");
        System.out.println("page.getNumber() = "+page.getNumber());
        System.out.println("page.getSize() = "+page.getSize());
        System.out.println("page.getTotalPages() = "+page.getTotalPages());
        System.out.println("page.previousPageable() = "+page.previousPageable());
        System.out.println("page.nextPageable() = "+page.nextPageable());

        System.out.println("page.isFirst() = "+page.isFirst());
        System.out.println("page.isLast() = "+page.isLast());

        System.out.println("page.hasPrevious() = "+page.hasPrevious());
        System.out.println("page.hasNext() = "+page.hasNext());
        System.out.println("*****************************************");

        System.out.println("list.size = " + page.getContent().size());
        //list.forEach(b->System.out.println(b.getBno() +" = " + b.getReplyList().size()));

        page.getContent().forEach(b->{
            System.out.println(b.getBno()+" | " + b.getSubject());
            b.getRepliesList().forEach(r->{
                System.out.println("====> " +r.getRno()+" | " +r.getContent()+" | "+ r.getRno());
            });
            System.out.println();
        });
    }



}//클래스끝
