    package com.project.bloggiator.controller;

    import com.project.bloggiator.entity.User;
    import com.project.bloggiator.repo.UserRepository;
    import com.project.bloggiator.service.UserService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.web.bind.annotation.*;

    import java.security.Principal;
    import java.util.HashMap;
    import java.util.Map;

    @RestController
    @CrossOrigin(origins = { "http://localhost:5500", "http://127.0.0" +
            ".1:5500", "https://05ambuj.github.io", "http://localhost:5501",
            "http://127.0.0" +
                    ".1:5501" })    @RequestMapping("/user")
    public class UserController {

        @Autowired
        private UserService userService;

        @Autowired
        private UserRepository userRepository;
        @PutMapping
         public ResponseEntity<?> updateUser(@RequestBody User user)
        {
            Authentication authentication=
                    SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User userInDB = userService.findByUserName(userName);
            userInDB.setUserName(user.getUserName());
            userInDB.setPassword(user.getPassword());
            userService.saveNewUser(userInDB);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        @GetMapping("/profile")
        public ResponseEntity<User> getUserProfile(Principal principal) {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            User user = userService.findByUserName(principal.getName());
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            // Ensure that you are returning only the required details
            return ResponseEntity.ok(user);
        }
        @GetMapping("/check-username")
        public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
            User user = userService.findByUserName(username);
            boolean available = user == null;
            Map<String, Boolean> response = new HashMap<>();
            response.put("available", available);
            return ResponseEntity.ok(response);
        }

        @DeleteMapping
        public ResponseEntity<?> deleteUserById() {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();
            userRepository.deleteByUserName(authentication.getName());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
