package com.laura.taskmanager.service;

import org.springframework.stereotype.Service;

import com.laura.taskmanager.dto.RegisterRequest;
import com.laura.taskmanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.laura.taskmanager.model.User;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }
}
