
package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.Complaint;
import com.example.demo.repository.ComplaintRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepository;

    // ==============================
    // MY COMPLAINTS PAGE
    // ==============================

    @GetMapping("/mycomplaints")
    public String myComplaints(
            Model model,
            HttpSession session) {

        // Get logged-in citizen email
        String citizenEmail =
                (String) session.getAttribute(
                        "loggedInUserEmail");

        // If citizen is not logged in
        if (citizenEmail == null) {
            return "redirect:/login";
        }

        // Get ONLY logged-in citizen's complaints
        List<Complaint> complaints =
                complaintRepository
                .findByCitizenEmail(citizenEmail);

        // ==============================
        // DUPLICATE CHECK
        // ==============================

        for (Complaint complaint : complaints) {

            if (complaint.getAiCategory() == null
                    || complaint.getLocation() == null
                    || complaint.getLocation().trim().isEmpty()) {

                complaint.setDuplicateWarning(
                        "No Duplicate");

                continue;
            }

            List<Complaint> similarComplaints =
                    complaintRepository
                    .findByLocationContainingIgnoreCaseAndAiCategory(
                            complaint.getLocation(),
                            complaint.getAiCategory()
                    );

            boolean duplicateFound = false;

            for (Complaint similar : similarComplaints) {

                // Do not compare complaint with itself
                if (similar.getId() != null
                        && complaint.getId() != null
                        && !similar.getId().equals(
                                complaint.getId())) {

                    duplicateFound = true;
                    break;
                }
            }

            if (duplicateFound) {

                complaint.setDuplicateWarning(
                        "Similar Complaint Already Exists");

            } else {

                complaint.setDuplicateWarning(
                        "No Duplicate");
            }
        }

        model.addAttribute(
                "complaints",
                complaints);

        return "mycomplaints";
    }
}

