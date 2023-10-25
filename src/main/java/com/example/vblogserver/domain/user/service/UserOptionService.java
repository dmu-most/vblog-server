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

    public void addUserOption(User user, Option option) {
        List<UserOption> currentOptions = user.getUserOptions();


        if (currentOptions.size() >= 3) {
            throw new RuntimeException("A user can select up to 3 options.");
        }

        UserOption newUserOption = new UserOption();
        newUserOption.setUser(user);
        newUserOption.setOption(option);

        // save the new selection in the database
        userOptionRepository.save(newUserOption);
    }

    public List<UserOption> saveUserOptions(String loginId, List<OptionType> options) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("Invalid login id: " + loginId));

        // Check if the user has already selected options and remove them if so
        Long userId = user.getId(); // assuming User object has getId() method
        List<UserOption> existingOptions = userOptionRepository.findByUserId(userId);
        if (!existingOptions.isEmpty()) {
            userOptionRepository.deleteAll(existingOptions);
        }

        // Save the new options
        List<UserOption> newOptions = new ArrayList<>();
        for (var type : options) { // changed from optionType to options
            Option option = optionRepository.findByType(type).orElseThrow(()
                    -> new RuntimeException("Invalid Option Type"));
            UserOption newUserOption = new UserOption();
            newUserOption.setUser(user);
            newUserOption.setOption(option);

            newOptions.add(newUserOption);

            // save the new selection in the database
            userOptionRepository.save(newUserOption);
        }

        return newOptions;
    }
}

