package com.myproject.service;

import com.myproject.entity.User;
import com.myproject.payload.LoginDto;
import com.myproject.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private UserRepository userRepository;
    private EmailService emailService;
    private final JWTService jwtService;


    public UserService(UserRepository userRepository, EmailService emailService, JWTService jwtService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
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
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)));
            user.setRole("ROLE_USER");
            userRepository.save(user); // Now save in DB after verification
            emailService.sendSuccessEmail(user.getEmail());
            return "Email verified successfully! Registration completed.";
        }

        return "Invalid or expired verification token.";
    }

    public String verifyLogin(LoginDto loginDto) {
        Optional<User> opUser = userRepository.findByEmail(loginDto.getEmail());
        if (opUser.isPresent()) {
            User user = opUser.get();
            if (BCrypt.checkpw(loginDto.getPassword(), user.getPassword())) {
                String token = jwtService.generateToken(user.getEmail());
                return token;
            }
        }else {
            return null;
        }
        return null;
    }

    public String updateUser(String token, User updatedUser) {
        // Extract token without modifying the parameter
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        String email = jwtService.extractEmail(jwtToken);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setFirstName(updatedUser.getFirstName());
            user.setMiddleName(updatedUser.getMiddleName());
            user.setLastName(updatedUser.getLastName());
            user.setPhoneNo(updatedUser.getPhoneNo());
            user.setAddress(updatedUser.getAddress());
            userRepository.save(user);
            return "User updated successfully";
        }
        return "Invalid token or user not found";
    }



}