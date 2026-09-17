package com.laura.taskmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.laura.taskmanager.model.Task;
import com.laura.taskmanager.model.User;
import com.laura.taskmanager.repository.TaskRepository;
import com.laura.taskmanager.repository.UserRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<Task> getTasks(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByOwnerId(user.getId());
    }
}