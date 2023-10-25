package com.example.vblogserver.domain.user.controller;

import com.example.vblogserver.domain.user.entity.Option;
import com.example.vblogserver.domain.user.entity.OptionType;
import com.example.vblogserver.domain.user.entity.UserOption;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.domain.user.service.OptionService;
import com.example.vblogserver.domain.user.service.UserOptionService;
import com.example.vblogserver.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/options")
public class UserOptionController {
    private final OptionService optionService;
    private final UserOptionService userOptionService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public UserOptionController(OptionService optionService, UserOptionService userOptionService, JwtService jwtService, UserRepository userRepository) {
        this.optionService = optionService;
        this.userOptionService = userOptionService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public ResponseEntity<List<Option>> getAllOptions() {
        List<Option> options = optionService.getAllOptions();
        return new ResponseEntity<>(options, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<List<UserOption>> updateUserOptions(HttpServletRequest request,
                                                              @RequestBody List<OptionType> options){
        String token = request.getHeader("Authorization").substring(7);  // "Bearer " 제거
        String loginId = jwtService.extractId(token)
                .orElseThrow(() -> new RuntimeException("Invalid access token"));

        List<UserOption> updatedUserOptions = userOptionService.saveUserOptions(loginId, options);

        return ResponseEntity.ok(updatedUserOptions);
    }
}
