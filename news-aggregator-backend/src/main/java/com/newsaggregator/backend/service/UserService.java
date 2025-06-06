package com.newsaggregator.backend.service;

import com.newsaggregator.backend.dto.AuthResponseDTO;
import com.newsaggregator.backend.dto.LoginRequestDTO;
import com.newsaggregator.backend.dto.UserRegistrationDTO;
import com.newsaggregator.backend.entity.User;
import com.newsaggregator.backend.exception.DuplicateResourceException;
import com.newsaggregator.backend.exception.ResourceNotFoundException;
import com.newsaggregator.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // This will be needed later
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Will be injected by Spring Security later

    // Constructor injection
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        logger.info("Attempting to register user: {}", registrationDTO.getUsername());

        // Check for duplicate username
        if (userRepository.findByUsername(registrationDTO.getUsername()).isPresent()) {
            logger.warn("Registration failed: Username '{}' already exists.", registrationDTO.getUsername());
            throw new DuplicateResourceException("User", "username", registrationDTO.getUsername());
        }

        // Check for duplicate email
        if (userRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            logger.warn("Registration failed: Email '{}' already registered.", registrationDTO.getEmail());
            throw new DuplicateResourceException("User", "email", registrationDTO.getEmail());
        }

        User newUser = new User();
        newUser.setUsername(registrationDTO.getUsername());
        newUser.setEmail(registrationDTO.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(registrationDTO.getPassword())); // Hash the password

        User savedUser = userRepository.save(newUser);
        logger.info("User '{}' registered successfully with ID: {}", savedUser.getUsername(), savedUser.getId());

        // For now, return basic info. JWT token will be added later.
        return new AuthResponseDTO(savedUser.getUsername(), savedUser.getEmail(), null);
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO loginUser(LoginRequestDTO loginRequest) {
        logger.info("Attempting to login user: {}", loginRequest.getUsername());

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> {
                    logger.warn("Login failed: User '{}' not found.", loginRequest.getUsername());
                    return new ResourceNotFoundException("User", "username", loginRequest.getUsername());
                });

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            logger.warn("Login failed for user '{}': Invalid credentials.", loginRequest.getUsername());
            throw new IllegalArgumentException("Invalid username or password"); // Use a more generic message for security
        }

        logger.info("User '{}' logged in successfully.", user.getUsername());
        // For now, return basic info. JWT token will be added later.
        return new AuthResponseDTO(user.getUsername(), user.getEmail(), null);
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long userId) {
        logger.debug("Finding user by ID: {}", userId);
        return userRepository.findById(userId);
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }
}