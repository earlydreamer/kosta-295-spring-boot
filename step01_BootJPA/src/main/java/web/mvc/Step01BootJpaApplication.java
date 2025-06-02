package web.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@SpringBootApplication (exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
public class Step01BootJpaApplication {
    public static void main(String[] args) {
        SpringApplication.run(Step01BootJpaApplication.class, args);
    }

}
