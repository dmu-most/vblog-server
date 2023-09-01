package com.example.vblogserver.init.tmp;

import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class TmpUser implements CommandLineRunner {
    private final UserRepository userRepository;

    public TmpUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        User testUser = User.builder()
                .email("test@testmail.com")
                .loginid("testuser")
                .password("test")
                .username("Test User")
                .imageUrl("https://example.com/profile.jpg")
                .socialId(null)
                .build();
        userRepository.save(testUser);
    }
}
