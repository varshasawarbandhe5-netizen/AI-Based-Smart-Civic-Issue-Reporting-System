package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // ==============================
    // REGISTER PAGE
    // ==============================

    @GetMapping("/register")
    public String registerPage() {

        return "register";
    }

    // ==============================
    // REGISTER USER
    // ==============================

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute User user) {

        userService.registerUser(user);

        return "redirect:/login";
    }

    // ==============================
    // LOGIN PAGE
    // ==============================

    @GetMapping("/login")
    public String loginPage() {

        return "login";
    }

    // ==============================
    // LOGIN USER
    // ==============================

    @PostMapping("/login")
    public String loginUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session) {

        System.out.println(
                "Login Attempt = " + email);

        User user =
                userService.getUserByEmail(email);

        // ==============================
        // CHECK USER
        // ==============================

        if (user != null) {

            System.out.println(
                    "User Found = "
                    + user.getEmail());

            // ==============================
            // CHECK PASSWORD
            // ==============================

            if (user.getPassword()
                    .equals(password)) {

                // ==============================
                // LOGIN SUCCESS
                // ==============================

                System.out.println(
                        "LOGIN SUCCESS - "
                        + user.getEmail());

                // Save logged-in user's email
                session.setAttribute(
                        "loggedInUserEmail",
                        user.getEmail());

                // Save logged-in user's ID
                session.setAttribute(
                        "loggedInUserId",
                        user.getId());

                // Save logged-in user's name
                session.setAttribute(
                        "loggedInUserName",
                        user.getFullName());

                System.out.println(
                        "Session Email = "
                        + session.getAttribute(
                                "loggedInUserEmail"));

                System.out.println(
                        "Redirecting to Dashboard");

                return "redirect:/dashboard";
            }

            System.out.println(
                    "LOGIN FAILED - Wrong Password");

        } else {

            System.out.println(
                    "LOGIN FAILED - User Not Found");
        }

        return "redirect:/login";
    }
}