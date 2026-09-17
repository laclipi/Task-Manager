package com.laura.taskmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.laura.taskmanager.dto.TaskRequest;
import com.laura.taskmanager.dto.TaskResponse;
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

    public List<TaskResponse> getTasks(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Task> tasks = taskRepository.findByOwnerId(user.getId());

        List<TaskResponse> responses = new java.util.ArrayList<>();

        for (Task task : tasks) {
            TaskResponse response = new TaskResponse();
            response.setId(task.getId());
            response.setTitle(task.getTitle());

            responses.add(response);
        }

        return responses;
    }

    public TaskResponse createTask(String email, TaskRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setOwner(user);

        taskRepository.save(task);

        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());

        return response;
    }
}
