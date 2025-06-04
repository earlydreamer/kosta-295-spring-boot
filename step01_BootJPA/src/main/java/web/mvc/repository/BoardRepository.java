package web.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import web.mvc.domain.Board;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    /**
     * 글번호를 인수로 받아 인수보다 큰 레코드를 삭제
     * DML문장일 경우 반드시 @Modifying 필요
     */
    @Modifying
    @Query(value = "delete from Board b where b.id>?1")
    void deleteGreaterById(Long id);


    /**
     * 글번호 또는 제목을 인수로 전달받아 해당하는 레코드 검색
     *
     */

//    @Query(value= "select b from Board b where b.id=?1 or b.title=?2")
    @Query(value = "select * from board where id=?1 or title like ?2", nativeQuery=true)
    List<Board> findByNoOrTitleTest(Long id, String title);

    Long id(Long id);

    /**
     * 글번호, 제목, 작성자에 대한 레코드 검색
     */

    @Query(value=
            "select b from Board b where b.id=:#{$board.id} or b.title=:#{$board.title} or b.writer=:#{$board.writer}"
    )
    List<Board> findByWhere(@Param("board") Board board);


    List<Board> findByIdLessThanAndWriter(long id, String writer);
    List<Board> findByIdLessThanOrWriter(long l, String writer);
}


