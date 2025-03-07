package com.myproject.controller;


import com.myproject.entity.User;
import com.myproject.payload.JwtToken;
import com.myproject.payload.LoginDto;
import com.myproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //http://localhost:8080/api/users/register
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        String response = userService.registerUser(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //http://localhost:8080/api/users/verify?token=
    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String token) {
        String response = userService.verifyUser(token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        String token = userService.verifyLogin(loginDto);
        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(token);
        jwtToken.setType("JWT");
        if (token!=null) {
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid credentials!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestHeader("Authorization") String token, @RequestBody User updatedUser) {
        System.out.println("Received Token: " + token);  // Debugging
        String response = userService.updateUser(token, updatedUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}