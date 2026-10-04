package com.rabtech.task05.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class SecureController {

    @GetMapping("/api/secure/user")
    public ResponseEntity<Map<String, Object>> getUserProfile(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "You are authenticated");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/admin/dashboard")
    public ResponseEntity<Map<String, Object>> getAdminDashboard(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Admin Dashboard");
        response.put("username", authentication.getName());
        response.put("role", "ADMIN");
        return ResponseEntity.ok(response);
    }
}

