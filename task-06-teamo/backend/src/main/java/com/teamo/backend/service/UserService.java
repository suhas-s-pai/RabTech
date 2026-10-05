package com.teamo.backend.service;

import com.teamo.backend.dto.UserDto;
import com.teamo.backend.entity.Role;
import com.teamo.backend.entity.User;
import com.teamo.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<UserDto> getAllEmployees() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.EMPLOYEE)
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    public UserDto getUserDtoByEmail(String email) {
        return UserDto.fromEntity(getUserByEmail(email));
    }
}