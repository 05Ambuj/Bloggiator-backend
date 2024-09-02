package com.project.bloggiator.controller;

import com.project.bloggiator.entity.BlogEntity;
import com.project.bloggiator.entity.User;
import com.project.bloggiator.service.BloggiatorService;
import com.project.bloggiator.service.EmailService;
import com.project.bloggiator.service.UserDetailsServiceImpl;
import com.project.bloggiator.service.UserService;
import com.project.bloggiator.utils.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"}) // Allow CORS for specific origins
@RequestMapping("/public")

public class PublicController {
    private static final Logger log = LoggerFactory.getLogger(PublicController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BloggiatorService bloggiatorService;

    @GetMapping("/health-check")
    public String healthCheck() {
        return "Ok";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
            if (userService.findByUserName(user.getUserName()) != null) {
                return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
            }
            if (userService.saveNewUser(user)) {
                emailService.sendRegistrationEmail(user.getEmail(), user.getUserName());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        try {
            User user = userService.findByUserName(username);
            boolean available = user == null;
            log.info("Checked username: {}, User: {}", username, user);
            Map<String, Boolean> response = new HashMap<>();
            response.put("available", available);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error checking username", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("available", false));
        }
    }
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        try {
            User user = userService.findByEmail(email);
            boolean available = user == null;
            log.info("Checked email: {}, User: {}", email, user);
            Map<String, Boolean> response = new HashMap<>();
            response.put("available", available);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error checking email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("available", false));
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllBlogs() {
        List<BlogEntity> allBlogs = bloggiatorService.getAllBlogs(); // Method to retrieve all blogs
        if (allBlogs != null && !allBlogs.isEmpty()) {
            return new ResponseEntity<>(allBlogs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
                                              );
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            log.error("Incorrect username or password: {}", e.getMessage());
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Exception occurred while creating authentication token: {}", e.getMessage(), e);
            return new ResponseEntity<>("Authentication failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}