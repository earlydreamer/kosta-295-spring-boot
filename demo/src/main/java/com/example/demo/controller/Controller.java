package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class Controller {
    @GetMapping("/")
    public String hello() {
        log.info("로그 테스트");
        return "helloworld";

    }

    @GetMapping("/test")
    public List<String> test() {
        System.out.println("test요청됨...");
        return List.of("A","B","C");
    }


}
