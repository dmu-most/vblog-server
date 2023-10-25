package com.example.vblogserver.domain.user.service;

import com.example.vblogserver.domain.user.entity.OptionType;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserOptionService {

    private final UserRepository userRepository;

    public UserOptionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Map<String, Object> saveUserOptions(String loginId, List<OptionType> options) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("Invalid login id: " + loginId));

        if (options.isEmpty() || options.size() > 3) {
            throw new RuntimeException("1~3개의 카테고리를 선택해주세요.");
        }

        // 사용자가 선택한 옵션들 검증
        for (OptionType option : options) {
            if (!Arrays.asList(OptionType.values()).contains(option)) {
                throw new RuntimeException("Invalid Option Type: " + option);
            }
        }

        // Save the new options
        user.setOptions(new HashSet<>(options));
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("isSelected", !options.isEmpty());
        response.put("categorys", options);

        return response;
    }

    public Map<String, Object> updateUserOptions(String loginId, List<OptionType> options) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("Invalid login id: " + loginId));

        if (options.isEmpty() || options.size() > 3) {
            throw new RuntimeException("1~3개의 카테고리를 선택해주세요.");
        }

        // 사용자가 선택한 옵션들 검증
        for (OptionType option : options) {
            if (!Arrays.asList(OptionType.values()).contains(option)) {
                throw new RuntimeException("Invalid Option Type: " + option);
            }
        }

        // Update the options
        user.setOptions(new HashSet<>(options));
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("isSelected", !options.isEmpty());
        response.put("categorys", options);

        return response;
    }

}

