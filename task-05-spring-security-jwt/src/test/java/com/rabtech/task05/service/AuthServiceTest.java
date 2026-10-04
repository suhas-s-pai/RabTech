package com.rabtech.task05.service;

import com.rabtech.task05.dto.AuthResponse;
import com.rabtech.task05.dto.LoginRequest;
import com.rabtech.task05.dto.RegisterRequest;
import com.rabtech.task05.entity.Role;
import com.rabtech.task05.entity.User;
import com.rabtech.task05.repository.UserRepository;
import com.rabtech.task05.security.CustomUserDetailsService;
import com.rabtech.task05.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should register new user and encode password using BCrypt")
    void testRegisterUser() {
        RegisterRequest request = new RegisterRequest("suhas", "password123", Role.USER);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "suhas", "encodedPass", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(userRepository.existsByUsername("suhas")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPasswordHash");
        when(userDetailsService.loadUserByUsername("suhas")).thenReturn(userDetails);
        when(jwtService.generateToken(any(), eq("USER"))).thenReturn("mock.jwt.token");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getUsername()).isEqualTo("suhas");
        assertThat(response.getRole()).isEqualTo(Role.USER);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("$2a$10$encodedPasswordHash");
    }

    @Test
    @DisplayName("Should reject registration with duplicate username")
    void testRegisterDuplicateUsername() {
        RegisterRequest request = new RegisterRequest("suhas", "password123", Role.USER);
        when(userRepository.existsByUsername("suhas")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username is already taken");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should login user with valid credentials")
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("suhas", "password123");
        User user = new User("suhas", "$2a$10$encodedPasswordHash", Role.USER);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "suhas", "$2a$10$encodedPasswordHash", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(userRepository.findByUsername("suhas")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("suhas")).thenReturn(userDetails);
        when(jwtService.generateToken(any(), eq("USER"))).thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
    }
}

