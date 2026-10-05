package com.teamo.backend.repository;

import com.teamo.backend.entity.Task;
import com.teamo.backend.entity.TaskStatus;
import com.teamo.backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedTo(User user);

    List<Task> findByCreatedBy(User user);

    List<Task> findByStatus(TaskStatus status);
}