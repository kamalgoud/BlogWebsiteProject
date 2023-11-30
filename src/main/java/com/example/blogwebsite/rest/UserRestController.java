package com.example.blogwebsite.rest;

import com.example.blogwebsite.entities.User;
import com.example.blogwebsite.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/saveRegisteredUser")
    public String saveRegisteredUser(@RequestParam String name,
                                     @RequestParam String email,
                                     @RequestParam String password,
                                     @RequestParam String confirmPassword) {

        if (userRepository.findByEmail(email) != null ||
                !password.equals(confirmPassword) || name == null || name.trim().isEmpty()) {
            return "invalid credenials";
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("{noop}" + password);
        user.setAuthority("author");
        userRepository.save(user);
        return "User Registered Successfully";
    }
}
