package com.laura.taskmanager.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laura.taskmanager.model.Task;
import com.laura.taskmanager.service.TaskService;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public List<Task> getTasks(Authentication authentication) {

        String email = authentication.getName();

        return taskService.getTasks(email);
    }
}