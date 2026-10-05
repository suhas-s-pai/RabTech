package com.teamo.backend.controller;

import com.teamo.backend.dto.UserDto;
import com.teamo.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users API", description = "Endpoints for managing and querying user profiles")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get All Users", description = "Retrieve a list of all registered users without passwords.")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/employees")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get All Employees", description = "Retrieve employees available for task assignment (Managers only).")
    public ResponseEntity<List<UserDto>> getEmployees() {
        return ResponseEntity.ok(userService.getAllEmployees());
    }
}