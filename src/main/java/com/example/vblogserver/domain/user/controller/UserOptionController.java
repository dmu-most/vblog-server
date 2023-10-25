package com.example.vblogserver.domain.user.controller;

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

import java.util.*;
import java.util.stream.Collectors;

@RestController
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

    @GetMapping("/options")
    public ResponseEntity<List<OptionType>> getAllOptions() {
        List<OptionType> options = Arrays.asList(OptionType.values());
        return new ResponseEntity<>(options, HttpStatus.OK);
    }

    @PostMapping("/options")
    public ResponseEntity<Map<String, Object>> saveUserOptions(HttpServletRequest request, @RequestBody List<OptionType> options) {
        String token = request.getHeader("Authorization").substring(7);
        String loginId = jwtService.extractId(token)
                .orElseThrow(() -> new RuntimeException("Invalid access token"));

        List<UserOption> savedOptions = userOptionService.saveUserOptions(loginId, options);

        Map<String, Object> response = new HashMap<>();

        if (!savedOptions.isEmpty()) {
            List<String> selectedOptions = savedOptions.stream()
                    .map(userOption -> userOption.getOption().getType().name())
                    .collect(Collectors.toList());
            response.put("isSelected", true);
            response.put("type", selectedOptions);
        } else {
            response.put("isSelected", false);
            response.put("type", new ArrayList<>());
        }

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/myinfo/options")
    public ResponseEntity<Map<String, Object>> updateUserOptions(HttpServletRequest request,
                                                                 @RequestBody List<OptionType> options) {
        String token = request.getHeader("Authorization").substring(7);
        String loginId = jwtService.extractId(token)
                .orElseThrow(() -> new RuntimeException("Invalid access token"));

        List<UserOption> updatedOptions = userOptionService.updateUserOptions(loginId, options);

        Map<String, Object> response = new HashMap<>();

        if (!updatedOptions.isEmpty()) {
            List<String> selectedOptions = updatedOptions.stream()
                    .map(userOption -> userOption.getOption().getType().name())
                    .collect(Collectors.toList());
            response.put("isSelected", true);
            response.put("type", selectedOptions);
        } else {
            response.put("isSelected", false);
            response.put("type", new ArrayList<>());
        }

        return ResponseEntity.ok(response);
    }

}
