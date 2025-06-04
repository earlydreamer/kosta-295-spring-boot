package web.mvc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Step01BootJpaApplicationTests {

    @Test
    @DisplayName("등록하기")
    void contextLoads() {
        System.out.println("ContextLoads");
    }

    @Test
    @DisplayName("사전처리")
    @BeforeEach
    void beforeEach() {
        System.out.println("beforeEach");
    }


    @Test
    @DisplayName("사후처리")
    @AfterEach
    void after(){
        System.out.println("AfterEach");
    }

}
