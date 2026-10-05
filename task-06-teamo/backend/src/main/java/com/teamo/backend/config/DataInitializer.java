package com.teamo.backend.config;

import com.teamo.backend.entity.Role;
import com.teamo.backend.entity.User;
import com.teamo.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("krishna@rabtech.com").isEmpty()) {
            User manager = new User(
                    "Krishna",
                    "krishna@rabtech.com",
                    passwordEncoder.encode("password123"),
                    Role.MANAGER
            );
            userRepository.save(manager);
        }

        if (userRepository.findByEmail("suhas@rabtech.com").isEmpty()) {
            User employee = new User(
                    "Suhas",
                    "suhas@rabtech.com",
                    passwordEncoder.encode("password123"),
                    Role.EMPLOYEE
            );
            userRepository.save(employee);
        }
    }
}

