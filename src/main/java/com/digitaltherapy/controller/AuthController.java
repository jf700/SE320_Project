package com.digitaltherapy.controller;

import com.digitaltherapy.dto.AuthResponse;
import com.digitaltherapy.dto.LoginRequest;
import com.digitaltherapy.dto.RegisterRequest;
import com.digitaltherapy.entity.User;
import com.digitaltherapy.repository.UserRepository;
import com.digitaltherapy.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        //User saved = userRepository.save(user);
        //return ResponseEntity.ok(saved);
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/test")
    public String test() {
        return "Auth working";
    }

    /*
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        Optional<User> existing = userRepository.findByEmail(user.getEmail());

        if (existing.isPresent()) {
            return ResponseEntity.ok("Login successful");
        }

        return ResponseEntity.status(401).body("Invalid credentials");
    }

    // test
    @GetMapping("/test")
    public String test() {
        return "Auth controller is working!";
    }

    /*
    @GetMapping("/register-test")
    public AuthResponse registerTest() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");
        req.setPassword("123456");
        req.setName("Test User");

        return authService.register(req);
    }
    */

}
