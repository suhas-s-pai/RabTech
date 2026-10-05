package com.teamo.backend.service;

import com.teamo.backend.dto.TaskCreateRequest;
import com.teamo.backend.dto.TaskResponse;
import com.teamo.backend.dto.TaskReviewRequest;
import com.teamo.backend.dto.TaskSubmitRequest;
import com.teamo.backend.entity.Role;
import com.teamo.backend.entity.Task;
import com.teamo.backend.entity.TaskStatus;
import com.teamo.backend.entity.User;
import com.teamo.backend.repository.TaskRepository;
import com.teamo.backend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskResponse createTask(TaskCreateRequest request, String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Manager not found"));

        if (manager.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Only Managers can create tasks");
        }

        User employee = userRepository.findById(request.getAssignedToId())
                .orElseThrow(() -> new IllegalArgumentException("Assigned employee not found"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDeadline(request.getDeadline());
        task.setAssignedTo(employee);
        task.setCreatedBy(manager);
        task.setStatus(TaskStatus.ASSIGNED);

        Task saved = taskRepository.save(task);
        return TaskResponse.fromEntity(saved);
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() == Role.MANAGER) {
            return taskRepository.findByCreatedBy(user).stream()
                    .map(TaskResponse::fromEntity)
                    .collect(Collectors.toList());
        } else {
            return taskRepository.findByAssignedTo(user).stream()
                    .map(TaskResponse::fromEntity)
                    .collect(Collectors.toList());
        }
    }

    public List<TaskResponse> getTasksForEmployee(Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        return taskRepository.findByAssignedTo(employee).stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksForManager(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException("Manager not found"));

        return taskRepository.findByCreatedBy(manager).stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
        return TaskResponse.fromEntity(task);
    }

    public TaskResponse startTask(Long taskId, String employeeEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        User currentUser = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!task.getAssignedTo().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to start this task");
        }

        if (task.getStatus() != TaskStatus.ASSIGNED && task.getStatus() != TaskStatus.CHANGES_REQUESTED) {
            throw new IllegalStateException("Task cannot be started in status: " + task.getStatus());
        }

        task.setStatus(TaskStatus.IN_PROGRESS);
        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    public TaskResponse submitTask(Long taskId, TaskSubmitRequest request, String employeeEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        User currentUser = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!task.getAssignedTo().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to submit this task");
        }

        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only tasks in progress can be submitted");
        }

        task.setGithubUrl(request.getGithubUrl());
        task.setSubmissionComment(request.getSubmissionComment());
        task.setSubmittedAt(LocalDateTime.now());
        task.setStatus(TaskStatus.SUBMITTED);

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    public TaskResponse approveTask(Long taskId, TaskReviewRequest request, String managerEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        User currentUser = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (currentUser.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Only Managers can approve tasks");
        }

        if (task.getStatus() != TaskStatus.SUBMITTED) {
            throw new IllegalStateException("Only submitted tasks can be approved");
        }

        task.setManagerFeedback(request != null ? request.getFeedback() : "Approved");
        task.setReviewedAt(LocalDateTime.now());
        task.setStatus(TaskStatus.APPROVED);

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    public TaskResponse requestChanges(Long taskId, TaskReviewRequest request, String managerEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        User currentUser = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (currentUser.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Only Managers can request changes on tasks");
        }

        if (task.getStatus() != TaskStatus.SUBMITTED) {
            throw new IllegalStateException("Only submitted tasks can have changes requested");
        }

        task.setManagerFeedback(request != null ? request.getFeedback() : "Changes requested");
        task.setReviewedAt(LocalDateTime.now());
        task.setStatus(TaskStatus.CHANGES_REQUESTED);

        return TaskResponse.fromEntity(taskRepository.save(task));
    }
}
}