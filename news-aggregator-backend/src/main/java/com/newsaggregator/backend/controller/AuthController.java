package com.newsaggregator.backend.controller;

import com.newsaggregator.backend.dto.AuthResponseDTO;
import com.newsaggregator.backend.dto.LoginRequestDTO;
import com.newsaggregator.backend.dto.UserRegistrationDTO;
import com.newsaggregator.backend.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        logger.info("Received registration request for user: {}", registrationDTO.getUsername());
        AuthResponseDTO response = userService.registerUser(registrationDTO);
        logger.info("User {} registered successfully.", registrationDTO.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        logger.info("Received login request for user: {}", loginRequest.getUsername());
        AuthResponseDTO response = userService.loginUser(loginRequest);
        logger.info("User {} logged in successfully.", loginRequest.getUsername());
        return ResponseEntity.ok(response);
    }
}