package com.example.blogwebsite.controllers;

import com.example.blogwebsite.entities.User;
import com.example.blogwebsite.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecurityController {


    public UserRepository userRepository;

    @Autowired
    public SecurityController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/showMyLoginPage")
    public String showMyLoginPage() {
        return "login";
    }

    @RequestMapping("/access-denied")
    public String showAccessDenied() {
        return "access-denied";
    }

    @RequestMapping("/register")
    public String registerUser() {
        return "registration-form";
    }

    @RequestMapping("/saveRegisteredUser")
    public String saveRegisteredUser(@RequestParam String name,
                                     @RequestParam String email,
                                     @RequestParam String password,
                                     @RequestParam String confirmPassword) {

        if (userRepository.findByEmail(email) != null ||
                !password.equals(confirmPassword) || name==null || name.trim().isEmpty()) {
            return "registration-form";
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("{noop}" + password);
        user.setAuthority("author");
        userRepository.save(user);
        return "redirect:/";
    }

}
