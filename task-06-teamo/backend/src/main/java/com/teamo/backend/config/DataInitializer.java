package com.teamo.backend.config;

import com.teamo.backend.entity.Role;
import com.teamo.backend.entity.User;
import com.teamo.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createOrUpdateUser("Krishna", "krishna@teamo.com", "password123", Role.MANAGER);
        createOrUpdateUser("Suhas", "suhas@teamo.com", "password123", Role.EMPLOYEE);
        createOrUpdateUser("Krishna", "krishna@rabtech.com", "password123", Role.MANAGER);
        createOrUpdateUser("Suhas", "suhas@rabtech.com", "password123", Role.EMPLOYEE);
    }

    private void createOrUpdateUser(String name, String email, String rawPassword, Role role) {
        String lowerEmail = email.toLowerCase().trim();
        userRepository.findByEmailIgnoreCase(lowerEmail).ifPresentOrElse(
                existing -> {
                    boolean passwordMatches = passwordEncoder.matches(rawPassword, existing.getPassword());
                    if (!passwordMatches || existing.getRole() != role) {
                        existing.setPassword(passwordEncoder.encode(rawPassword));
                        existing.setRole(role);
                        userRepository.save(existing);
                        log.info("Updated demo user: {} ({})", lowerEmail, role);
                    } else {
                        log.info("Demo user verified: {} ({})", lowerEmail, role);
                    }
                },
                () -> {
                    User newUser = new User(
                            name,
                            lowerEmail,
                            passwordEncoder.encode(rawPassword),
                            role
                    );
                    userRepository.save(newUser);
                    log.info("Created demo user: {} ({})", lowerEmail, role);
                }
        );
    }
}

