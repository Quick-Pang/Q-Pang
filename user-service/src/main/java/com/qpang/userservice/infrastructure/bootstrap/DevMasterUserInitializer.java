package com.qpang.userservice.infrastructure.bootstrap;

import com.qpang.common.entity.UserStatus;
import com.qpang.userservice.domain.entity.MasterUser;
import com.qpang.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevMasterUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.master.username:master}")
    private String username;

    @Value("${app.seed.master.password:master1234}")
    private String password;

    @Value("${app.seed.master.email:master@qpang.local}")
    private String email;

    @Value("${app.seed.master.nickname:Master Admin}")
    private String nickname;

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername(username)) {
            log.info("Dev master user already exists: {}", username);
            return;
        }

        MasterUser masterUser = MasterUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .nickname(nickname)
                .status(UserStatus.APPROVED)
                .build();

        userRepository.saveAndFlush(masterUser);
        log.info("Dev master user created: {}", username);
    }
}
