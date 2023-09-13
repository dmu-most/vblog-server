package com.example.vblogserver.init.tmp;

import com.example.vblogserver.domain.user.entity.Role;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class TmpUser implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TmpUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        User testUser = User.builder()
            .loginId("testuser")
            .password("Test123!pw")
            .username("thisistest")
            .imageUrl("https://example.com/profile.jpg")
            .role(Role.USER)
            .socialId(null)
            .build();
        testUser.passwordEncode(passwordEncoder);
        userRepository.save(testUser);
    }
}
