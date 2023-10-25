package com.example.vblogserver.domain.user.service;

import com.example.vblogserver.domain.user.entity.Option;
import com.example.vblogserver.domain.user.entity.OptionType;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.entity.UserOption;
import com.example.vblogserver.domain.user.repository.OptionRepository;
import com.example.vblogserver.domain.user.repository.UserOptionRepository;
import com.example.vblogserver.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserOptionService {

    private final UserOptionRepository userOptionRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;

    public UserOptionService(UserOptionRepository userOptionRepository,
                             OptionRepository optionRepository,
                             UserRepository userRepository) {
        this.userOptionRepository = userOptionRepository;
        this.optionRepository = optionRepository;
        this.userRepository = userRepository;
    }

    public List<UserOption> createUserOptions(String loginId, List<OptionType> options) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("Invalid login id: " + loginId));

        if (options.isEmpty() || options.size() > 3) {
            throw new RuntimeException("1~3개의 카테고리를 선택해주세요.");
        }

        // Save the new options
        List<UserOption> newOptions = new ArrayList<>();
        for (var type : options) {
            Option option = optionRepository.findByType(type).orElseThrow(()
                    -> new RuntimeException("Invalid Option Type"));
            UserOption newUserOption = new UserOption();
            newUserOption.setUser(user);
            newUserOption.setOption(option);

            newOptions.add(newUserOption);
            userOptionRepository.save(newUserOption);
        }

        return newOptions;
    }

    public List<UserOption> updateUserOptions(String loginId, List<OptionType> options) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("Invalid login id: " + loginId));

        if (options.isEmpty() || options.size() > 3) {
            throw new RuntimeException("1~3개의 카테고리를 선택해주세요.");
        }

        Long userId = user.getId();
        List<UserOption> existingOptions = userOptionRepository.findByUserId(userId);
        if (!existingOptions.isEmpty()) {
            userOptionRepository.deleteAll(existingOptions);
        }

        return createUserOptions(loginId, options);
    }
}

