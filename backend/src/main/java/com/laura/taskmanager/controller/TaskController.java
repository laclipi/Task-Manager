package com.laura.taskmanager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.laura.taskmanager.dto.TaskRequest;
import com.laura.taskmanager.dto.TaskResponse;
import com.laura.taskmanager.model.Task;
import com.laura.taskmanager.service.TaskService;

import jakarta.validation.Valid;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public List<TaskResponse> getTasks(Authentication authentication) {

        String email = authentication.getName();

        return taskService.getTasks(email);
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        TaskResponse response = taskService.createTask(email, request);
        return ResponseEntity.status(201).body(response);
    }
}