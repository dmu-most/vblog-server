package com.example.vblogserver.domain.user.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/vblog")
    public String home() {
        return "vblog";
    }
}
