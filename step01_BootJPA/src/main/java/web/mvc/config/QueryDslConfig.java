package web.mvc.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration //Spring의 환경설정을 위한 클래스 (서버 Start 시 @Configuration 내의 @Bean으로 Bean 등록
@Slf4j
public class QueryDslConfig {
    @PersistenceContext //EntityManagerFactory로부터 EntityManager를 주입받는다.
    private EntityManager entityManager;
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        log.info("QueryDslConfig jpaQueryFactory call..");
        log.info("EntityManager:{}" , entityManager);
        return new JPAQueryFactory(entityManager);
    }

}
