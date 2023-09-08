package com.example.vblogserver.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.vblogserver.domain.user.dto.ResponseDto;
import com.example.vblogserver.domain.user.dto.UserSignUpDto;
import com.example.vblogserver.domain.user.service.UserService;
import com.example.vblogserver.global.jwt.service.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<UserSignUpDto> signUp(@Valid @RequestBody UserSignUpDto userSignUpDto, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getFieldError().getDefaultMessage());
        }
        userService.signUp(userSignUpDto);
        return ResponseEntity.status(HttpStatus.OK).body(userSignUpDto);
    }

    @GetMapping("/jwt-test")
    public String jwtTest() {
        return "jwtTest 요청 성공";
    }

    @GetMapping("/check-id")
    public ResponseEntity<ResponseDto> checkId(@RequestParam String loginId) {
        ResponseDto response = new ResponseDto();
        try {
            boolean isDuplicated = userService.isLoginIdDuplicated(loginId);
            response.setResult(!isDuplicated);
            response.setMessage(isDuplicated ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.");
        } catch (IllegalArgumentException e) {
            response.setResult(false);
            response.setMessage(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}