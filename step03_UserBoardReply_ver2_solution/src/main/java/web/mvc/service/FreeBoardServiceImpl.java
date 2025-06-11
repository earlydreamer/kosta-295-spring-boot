package web.mvc.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import web.mvc.domain.FreeBoard;
import web.mvc.exception.BasicException;
import web.mvc.exception.ErrorCode;
import web.mvc.repository.FreeBoardRepository;

import java.util.List;

@Service
@Transactional //Error  or RuntimeException이 발생하면 rollback된다.
@RequiredArgsConstructor
public class FreeBoardServiceImpl implements FreeBoardService {

    private final FreeBoardRepository freeBoardRepository;

    
    /**
     * 단순 FreeBoard 전체 검색 페이징 처리 X
     * */
    @Override
    public List<FreeBoard> selectAll() {
        return freeBoardRepository.findAll();

        ////fetch조인 사용하기 ///////////////////////
      //return freeBoardRepository.join01();
    }

    @Override
    public Page<FreeBoard> selectAll(Pageable pageable) {
        System.out.println("service selectAll(Page....");
       //return freeBoardRepository.findAll(pageable); //spring data JPA제공하는 메소드
        return freeBoardRepository.join05(pageable);
    }

    @Override
    public FreeBoard insert(FreeBoard board) {
        FreeBoard savedBoard = freeBoardRepository.save(board);
        System.out.println("savedBoard = " + savedBoard);
        return savedBoard;
    }

    /**
     * bno 로 Board 찾고 state로 조회수 올리기
     * */
    @Override
    public FreeBoard selectBy(Long bno, boolean state) {
        FreeBoard board = freeBoardRepository.findById(bno)
                .orElseThrow(()->new BasicException(ErrorCode.FAILED_DETAIL));

        //조회수를 증가시킨다. update free_board set readnum = readnum+1 ...
        if(state) board.setReadnum(board.getReadnum()+1);//변경감지 됐기때문에 update 실행
        
        return board;
    }

    /**
     *  board 내용 수정하기
     * */
    @Override
    public FreeBoard update(FreeBoard board) {

        FreeBoard bo = freeBoardRepository
                .findById(board.getBno())
                .orElseThrow(()->new BasicException(ErrorCode.FAILED_UPDATE));

        if(!bo.getPassword().equals(board.getPassword())){
            throw new BasicException(ErrorCode.FAILED_UPDATE); // 비밀번호 오류
        }
      //수정
        bo.setSubject(board.getSubject());
        bo.setContent(board.getContent());

        return bo;
    }

    /**
     * board 삭제하기
     * */
    @Override
    public void delete(Long bno, String password) {

        FreeBoard board = freeBoardRepository.findById(bno)
                .orElseThrow(()-> new BasicException(ErrorCode.FAILED_DELETE));

        if(!board.getPassword().equals(password))
            throw new BasicException(ErrorCode.FAILED_DELETE); //

        freeBoardRepository.delete(board);

    }
}
