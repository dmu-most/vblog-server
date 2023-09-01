package com.example.vblogserver.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.vblogserver.domain.user.dto.ResponseDto;
import com.example.vblogserver.domain.user.dto.UserSignUpDto;
import com.example.vblogserver.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserSignUpDto> signUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        userService.signUp(userSignUpDto);
        return ResponseEntity.status(HttpStatus.OK).body(userSignUpDto);
    }

    @GetMapping("/jwt-test")
    public String jwtTest() {
        return "jwtTest 요청 성공";
    }

    @GetMapping("/check-id")
    public ResponseEntity<ResponseDto> checkId(@RequestParam String loginid) {
        boolean isDuplicated = userService.isLoginIdDuplicated(loginid);
        ResponseDto response = new ResponseDto();
        response.setResult(isDuplicated);
        response.setMessage(isDuplicated ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-email")
    public ResponseEntity<ResponseDto> checkEmail(@RequestParam String email) {
        boolean isDuplicated = userService.isEmailDuplicated(email);
        ResponseDto response = new ResponseDto();
        response.setResult(isDuplicated);
        response.setMessage(isDuplicated ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다.");
        return ResponseEntity.ok(response);
    }
}
