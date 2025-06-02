package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/index")
      public void home(Model model) {
        model.addAttribute("message","Hello World");
        System.out.println("index요청됨...");
    }

}
