package web.mvc.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,length=100)
    private String title;
    private String content;
    private String writer;

    @CreationTimestamp
    private LocalDateTime insertDate;
    @UpdateTimestamp
    private LocalDateTime updateDate;


}
