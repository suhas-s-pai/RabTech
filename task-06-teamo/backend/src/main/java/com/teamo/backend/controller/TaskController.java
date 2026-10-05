package com.teamo.backend.controller;

import com.teamo.backend.dto.TaskCreateRequest;
import com.teamo.backend.dto.TaskResponse;
import com.teamo.backend.dto.TaskReviewRequest;
import com.teamo.backend.dto.TaskSubmitRequest;
import com.teamo.backend.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks API", description = "Endpoints for Task Creation, Workflow Management, Submission, and Approval")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create Task", description = "Managers create and assign a new task to an employee.")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskCreateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(request, authentication.getName()));
    }

    @GetMapping
    @Operation(summary = "Get User Tasks", description = "Get tasks relevant to the logged in user (assigned tasks for Employees, created tasks for Managers).")
    public ResponseEntity<List<TaskResponse>> getUserTasks(Authentication authentication) {
        return ResponseEntity.ok(taskService.getTasksForUser(authentication.getName()));
    }

    @GetMapping("/all")
    @Operation(summary = "Get All Tasks", description = "Retrieve all tasks in the system.")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Task By ID", description = "Retrieve task details by task ID.")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PutMapping("/{id}/start")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Start Task", description = "Employees mark an assigned task as IN_PROGRESS.")
    public ResponseEntity<TaskResponse> startTask(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(taskService.startTask(id, authentication.getName()));
    }

    @PutMapping("/{id}/submit")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Submit Task Work", description = "Employees submit completed work with GitHub repository URL and comments.")
    public ResponseEntity<TaskResponse> submitTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskSubmitRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(taskService.submitTask(id, request, authentication.getName()));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Approve Task", description = "Managers approve submitted task work with optional feedback.")
    public ResponseEntity<TaskResponse> approveTask(
            @PathVariable Long id,
            @RequestBody(required = false) TaskReviewRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(taskService.approveTask(id, request, authentication.getName()));
    }

    @PutMapping("/{id}/request-changes")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Request Changes", description = "Managers request changes on submitted task work with feedback.")
    public ResponseEntity<TaskResponse> requestChanges(
            @PathVariable Long id,
            @RequestBody(required = false) TaskReviewRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(taskService.requestChanges(id, request, authentication.getName()));
    }
}
}