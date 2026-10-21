package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    // ==============================
    // HOME PAGE
    // ==============================

    @GetMapping("/")
    public String home() {

        return "index";
    }

    // ==============================
    // DASHBOARD PAGE
    // ==============================

    @GetMapping("/dashboard")
    public String dashboard() {

        return "dashboard";
    }

    // ==============================
    // LOGOUT
    // ==============================

    @GetMapping("/logout")
    public String logout(
            HttpSession session) {

        // Destroy current session
        session.invalidate();

        System.out.println(
                "USER LOGGED OUT - SESSION CLEARED");

        return "redirect:/";
    }
}