package com.myproject.service;

import com.myproject.entity.User;
import com.myproject.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    private UserRepository userRepository;
    private EmailService emailService;


    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    // Store unverified users
    private Map<String, User> tempUsers = new HashMap<>();

    public String registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already registered.";
        }

        // Generate token
        String token = UUID.randomUUID().toString();
        // Store user and generated token temporarily in key value format
        tempUsers.put(token, user);
        // Send email
        emailService.sendVerificationEmail(user.getEmail(), token);
        return "A verification email has been sent. Please verify to complete registration.";
    }

    public String verifyUser(String token) {
        User user = tempUsers.remove(token); // Get user from temporary storage

        if (user != null) {
            userRepository.save(user); // Now save in DB after verification
            emailService.sendSuccessEmail(user.getEmail());
            return "Email verified successfully! Registration completed.";
        }

        return "Invalid or expired verification token.";
    }
}