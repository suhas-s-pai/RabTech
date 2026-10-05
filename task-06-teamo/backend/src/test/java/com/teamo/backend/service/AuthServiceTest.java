package com.teamo.backend.service;

import com.teamo.backend.dto.AuthRequest;
import com.teamo.backend.dto.AuthResponse;
import com.teamo.backend.dto.RegisterRequest;
import com.teamo.backend.entity.Role;
import com.teamo.backend.entity.User;
import com.teamo.backend.repository.UserRepository;
import com.teamo.backend.security.CustomUserDetailsService;
import com.teamo.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should register user with BCrypt password hashing and return AuthResponse without password")
    void testRegisterUser() {
        RegisterRequest request = new RegisterRequest("Suhas", "suhas@test.com", "password123", Role.EMPLOYEE);
        User savedUser = new User("Suhas", "suhas@test.com", "$2a$10$hashedPassword", Role.EMPLOYEE);
        savedUser.setId(1L);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "suhas@test.com", "$2a$10$hashedPassword", Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))
        );

        when(userRepository.findByEmailIgnoreCase("suhas@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("suhas@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userDetailsService.loadUserByUsername("suhas@test.com")).thenReturn(userDetails);
        when(jwtService.generateToken(any())).thenReturn("mock.jwt.token");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo("suhas@test.com");
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("Should throw exception when registering duplicate email")
    void testRegisterDuplicateEmail() {
        RegisterRequest request = new RegisterRequest("Suhas", "suhas@test.com", "password123", Role.EMPLOYEE);
        User existing = new User("Suhas", "suhas@test.com", "pass", Role.EMPLOYEE);

        when(userRepository.findByEmailIgnoreCase("suhas@test.com")).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("suhas@test.com")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is already registered");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void testLoginSuccess() {
        AuthRequest request = new AuthRequest("suhas@test.com", "password123");
        User user = new User("Suhas", "suhas@test.com", "$2a$10$hashedPassword", Role.EMPLOYEE);
        user.setId(1L);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "suhas@test.com", "$2a$10$hashedPassword", Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))
        );

        when(userRepository.findByEmailIgnoreCase("suhas@test.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("suhas@test.com")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("suhas@test.com")).thenReturn(userDetails);
        when(jwtService.generateToken(any())).thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getUser().getName()).isEqualTo("Suhas");
    }
}

