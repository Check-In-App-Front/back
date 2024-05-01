package com.ace.project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class MainController {
	
	@Operation(summary = "test", description = "메인 페이지 test")
    @GetMapping("/")
    public String main() {

        return "main test";
    }

}
