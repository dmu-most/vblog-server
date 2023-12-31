package com.example.vblogserver.domain.user.controller;

import java.util.Map;

import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.global.jwt.util.InvalidTokenException;
import com.example.vblogserver.global.jwt.util.TokenExpiredException;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.vblogserver.domain.user.dto.ResponseDto;
import com.example.vblogserver.domain.user.dto.UserSignUpDto;
import com.example.vblogserver.domain.user.dto.UserInfoDto;
import com.example.vblogserver.domain.user.service.UserService;
import com.example.vblogserver.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody UserSignUpDto userSignUpDto, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getFieldError().getDefaultMessage());
        }
        userService.signUp(userSignUpDto);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

        return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body("\"회원가입이 완료되었습니다.\"");
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

    @DeleteMapping("/users")
    public ResponseEntity<String> deleteUser(HttpServletRequest request) {
        String refreshToken = jwtService.extractRefreshToken(request)
                .orElseThrow(() -> new IllegalArgumentException("리프레시 토큰이 제공되지 않았습니다."));

        userService.deleteUser(refreshToken);

        return ResponseEntity.ok("\"회원 탈퇴가 완료되었습니다.\"");
    }

    @GetMapping("/users/info")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtService.extractAccessToken(request)
                .orElseThrow(() -> new InvalidTokenException("액세스 토큰이 없습니다."));

        try {
            User user = userService.getUserByAccessToken(accessToken);
            return ResponseEntity.ok(new UserInfoDto(user));
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ResponseDto(false, "만료된 액세스 토큰입니다.")
            );
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ResponseDto(false, "유효하지 않은 액세스 토큰입니다.")
            );
        }
    }

    @GetMapping("/myinfo/users/name")
    public ResponseEntity<UserInfoDto> getUserName(HttpServletRequest request) throws Exception {
        String accessToken = jwtService.extractAccessToken(request)
            .orElseThrow(() -> new Exception("액세스 토큰이 없습니다."));

        User user = userService.getUserByAccessToken(accessToken);

        return ResponseEntity.ok(new UserInfoDto(user));
    }

    @PatchMapping("/myinfo/users/name")
    public ResponseEntity<UserInfoDto> updateUsername(HttpServletRequest request, @RequestBody Map<String, String> updateRequest) throws Exception {
        String accessToken = jwtService.extractAccessToken(request)
            .orElseThrow(() -> new Exception("액세스 토큰이 없습니다."));

        User user = userService.getUserByAccessToken(accessToken);

        // 클라이언트로부터 전달받은 새로운 이름으로 사용자의 이름 변경
        String newUsername = updateRequest.get("username");
        if (newUsername != null && !newUsername.isEmpty()) {
            userService.updateUsername(user, newUsername);
        }

        return ResponseEntity.ok(new UserInfoDto(user));
    }
}