package web.mvc.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import web.mvc.domain.FreeBoard;
import web.mvc.domain.Reply;
import web.mvc.dto.FreeBoardDTO;
import web.mvc.dto.ReplyDTO;
import web.mvc.service.FreeBoardService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/board")  // 요청 url이 /board/~ 이런 형식은 여기서 처리하겠다
@RequiredArgsConstructor
public class FreeBoardController {

    private final FreeBoardService freeBoardService;
    private final ModelMapper modelMapper;

    /** 이동하기
     *  데이터의 이동없이 단순 이동 처리 하기위해
     *  @PathVariable {url}의 url의 값을 가져온다
     * */
    @GetMapping("/{url}")
    public void url( @PathVariable String url){
        log.info("board url:"+url);
    }

    /** 전체 목록 보기
     *  list.jsp에 freeList로 FreeBoard타입의 list가 있어야 하므로
     *  Model model를 param으로 하여 model 에 list(객체)를 저장 하면 view에서
     *  requestScope에서 객체를 사용 할 수 있다
     * */
    @GetMapping("/list")
    public void list(Model model ){
        log.info("board list");

        List<FreeBoard> freeList = freeBoardService.selectAll(); //N+1문제 , 페이징처리안된다.
       model.addAttribute("freeList", freeList);

    }


    /** 글 등록 하기
     *  write.jsp의 form 데이터를 받아서 수정 합니다 -> form의 데이터를 받을때 그냥 객체로 받아야 합니다
     * */
    @PostMapping("/insert")
    public String insert(FreeBoardDTO freeBoardDTO){ //dto전달
   // public String insert(FreeBoard freeBoard){//entity 전달

        //DTO-> Entity로 변환
        FreeBoard freeBoard = modelMapper.map(freeBoardDTO,FreeBoard.class);
        FreeBoard savedBord = freeBoardService.insert(freeBoard);
        System.out.println("savedBoard = " + savedBord);

        return "redirect:/board/list"; //Post Redriect Get 패턴
    }

    /** 상세보기
     * list.jsp의 상세보기
     * flag 상태변수를 param으로 전달 받아 flag가 없으면 true로 조회수 증가시킨다.
     * @PathVariable {bno}의 bno의 값을 가져온다
     * Model 를 써서 뷰에서 사용할 객체 저장
     * requestScope에 객체를 저장했으니 이동할때 forward 방식으로 이동합니다 -> modelAndView와 같은 방식
     * */
    @GetMapping("/read/{bno}")
    public String read(@PathVariable String bno, Model model, String flag){
        log.info("board read: {} / {}" , bno, flag);

        boolean state = flag==null ?true : false;
        FreeBoard board = freeBoardService.selectBy(Long.parseLong(bno),state);//조회수증가

        //Entity --> dto 변환해서 뷰로 전달
        FreeBoardDTO boardDTO = modelMapper.map(board, FreeBoardDTO.class);
        model.addAttribute("board", boardDTO);
        System.out.println("============================");
        return "board/read"; //WEB-INF/views/board/read.jsp
    }

    /** 수정하기로 이동
     *  뷰에서 hidden으로 되어 있는 board.bno를 가져와서 해당 bno를 가진 board를 담아서
     *  update.jsp로 이동합니다
     *
     *  Model -> 객체 , view -> view 이름(url) , 두개를 포함한 의미 ModelAndView
     */
    @PostMapping("/updateForm")
    public ModelAndView updateForm(String bno, Model model){
        log.info("board updateForm bno:"+bno);
        FreeBoard board = freeBoardService.selectBy(Long.parseLong(bno),false);//조회수증가
        return new ModelAndView("/board/update", "board", board);
    }

    /** 수정하기
     * update.jsp의 form 데이터를 받아서 수정 한다. -> form의 데이터를 받을때 그냥 객체로 받는다.
//     * update 완료 후 read.jsp로 이동
     * board객체를 read에서 사용해야 되므로 redirect:/board/read/"+board.getBno()로 이동 방식으로 했습니다
     * forward 이동하면 기존의 Post방식의 요청이 있어 위에 read.jsp의 이동 방식인 Get과 맞지 않아 오류가 납니다
     * */
    @PostMapping("/update")
    public String update(FreeBoardDTO freeBoardDTO){
        FreeBoard board = modelMapper.map(freeBoardDTO, FreeBoard.class);
        freeBoardService.update(board);

        return "redirect:/board/read/"+board.getBno()+"?flag"; // PRG - 멱등성 유지 되어야한다.
    }


    /**
     * 삭제하기
     * */
    @PostMapping("/delete")
    public String delete(FreeBoard board){
        log.info("board delete bno:"+board.getBno());
        log.info("board delete password:"+board.getPassword());

        freeBoardService.delete(board.getBno(), board.getPassword());

        return "redirect:/board/list";

    }

}
