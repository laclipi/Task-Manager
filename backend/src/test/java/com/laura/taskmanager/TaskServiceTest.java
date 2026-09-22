package com.laura.taskmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.laura.taskmanager.dto.TaskRequest;
import com.laura.taskmanager.dto.TaskResponse;
import com.laura.taskmanager.model.User;
import com.laura.taskmanager.repository.TaskRepository;
import com.laura.taskmanager.repository.UserRepository;
import com.laura.taskmanager.service.TaskService;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTask() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        TaskRequest request = new TaskRequest();
        request.setTitle("Estudiar JWT");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(taskRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse response = taskService.createTask(
                "test@test.com",
                request);

        assertNotNull(response);
        assertEquals("Estudiar JWT", response.getTitle());
    }
}