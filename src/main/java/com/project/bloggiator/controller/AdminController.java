package com.project.bloggiator.controller;

import com.project.bloggiator.entity.User;
import com.project.bloggiator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = { "http://localhost:5500", "http://127.0.0" +
        ".1:5500", "https://05ambuj.github.io", "http://localhost:5501",
        "http://127.0.0" +
                ".1:5501" })
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // @GetMapping("/all-users")
    // public String getAllUsers(Model model) {
    // List<User> allUsers = userService.getAllUsers();
    // model.addAttribute("users", allUsers);
    // return "admin_get_users"; // Thymeleaf template name
    // }
    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        if (allUsers != null && !allUsers.isEmpty()) {
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/admin-signup")
    public void createAdmin(@RequestBody User user) {
        userService.saveAdmin(user);
    }
}
