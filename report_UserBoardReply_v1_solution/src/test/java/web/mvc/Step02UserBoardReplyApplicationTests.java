package web.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import web.mvc.repository.FreeBoardRepository;

//@SpringBootTest
//@Slf4j
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))

@DataJpaTest //jpa영속성에 관련된 test / @Component에 해당하는 부분은 적용안됨.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)//설정한 Db사용
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//@RequiredArgsConstructor
class Step02UserBoardReplyApplicationTests {//Spring IocContainer 가 관리안한다. (Spring Test context 영역에서 별도로 관리된다)
    //@Autowired
    //private  FreeBoardRepository freeRep;

    private final FreeBoardRepository freeRep;

     /*@Autowired
     public Step02UserBoardReplyApplicationTests(FreeBoardRepository freeRep){
          this.freeRep = freeRep;
     }*/

    @Test
    void contextLoads() {
        log.info("freeRep = {}" , freeRep);
    }

}
