package web.mvc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import web.mvc.domain.FreeBoard;
import web.mvc.domain.Reply;
import web.mvc.dto.ReplyDTO;
import web.mvc.service.FreeBoardService;
import web.mvc.service.ReplyService;

@Controller
@Slf4j
@RequestMapping("/reply") // 요청 url이 /reply/~ 이런 형식은 여기서 처리하겠다
@RequiredArgsConstructor
public class ReplyController {

    private final ModelMapper modelMapper;
    private final ReplyService replyService;

    /** 작성하기로 이동
     *  뷰에서 hidden으로 되어 있는 board.bno를 가져와서
     *  reply의 write.jsp로 bno를 포함하여 이동합니다
     */
    @PostMapping("/writeForm")
    public ModelAndView writeForm(String bno){//부모글의 글번호
        log.info("reply updateForm bno:"+bno);

        return new ModelAndView("/reply/write", "bno", bno);
    }


    /** 작성하기
     *  bno에 해당하는 board에 게시글을 등록합니다
     */
    @PostMapping("/insert")
    public String insertForm(Long bno, ReplyDTO replyDTO ) { //부모글번호 /  댓글내용
        log.info("reply insertForm bno:{},content:{}",bno,replyDTO.getContent());

        //dto -> Entity 변환
        Reply reply =  modelMapper.map(replyDTO, Reply.class);
        reply.setFreeBoard(new FreeBoard(bno));//댓글의 부모 글번호 설정

        replyService.insert(reply);

        return "redirect:/board/read/"+bno+"?flag"; //상세보기로 갈때 조회수 증가 안함.(flag 전달)
    }

    /** 삭제하기
     *  bno에 해당하는 board의 reply를 삭제 합니다
     * */
    @GetMapping("/delete/{rno}/{bno}")
    public String delete(@PathVariable long rno, @PathVariable int bno){
        replyService.delete(rno);

        return "redirect:/board/read/"+bno+"?flag";
    }
}
