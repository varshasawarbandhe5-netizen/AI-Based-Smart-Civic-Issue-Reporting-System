package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Complaint;
import com.example.demo.repository.ComplaintRepository;
import com.example.demo.service.ComplaintService;

@Controller
public class AdminController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private ComplaintRepository complaintRepository;


    // ==============================
    // ADMIN LOGIN PAGE
    // ==============================

    @GetMapping("/adminlogin")
    public String adminLoginPage() {
        return "adminlogin";
    }


    // ==============================
    // ADMIN LOGIN
    // ==============================

    @PostMapping("/adminlogin")
    public String adminLogin(
            @RequestParam String email,
            @RequestParam String password) {

        if (email.equals("admin@gmail.com")
                && password.equals("admin123")) {

            return "redirect:/admindashboard";
        }

        return "redirect:/adminlogin";
    }


    // ==============================
    // ADMIN DASHBOARD
    // ==============================

    @GetMapping("/admindashboard")
    public String adminDashboard(Model model) {

        List<Complaint> complaints =
                complaintService.getAllComplaints();

        // Check duplicate warning
        checkDuplicateWarnings(complaints);

        model.addAttribute(
                "complaints",
                complaints);

        addStatistics(model);

        return "admindashboard";
    }


    // ==============================
    // DUPLICATE CHECK
    // ==============================

    private void checkDuplicateWarnings(
            List<Complaint> complaints) {

        for (Complaint complaint : complaints) {

            // If AI category or location is missing
            if (complaint.getAiCategory() == null
                    || complaint.getLocation() == null
                    || complaint.getLocation()
                            .trim()
                            .isEmpty()) {

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


            for (Complaint similar :
                    similarComplaints) {

                // Don't compare complaint with itself
                if (similar.getId() != null
                        && complaint.getId() != null
                        && !similar.getId()
                                .equals(complaint.getId())) {

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
    }


    // ==============================
    // SEARCH COMPLAINTS
    // ==============================

    @GetMapping("/search")
    public String searchComplaints(
            @RequestParam("keyword") String keyword,
            Model model) {

        List<Complaint> complaints;

        if (keyword == null
                || keyword.trim().isEmpty()) {

            complaints =
                    complaintService.getAllComplaints();

        } else {

            complaints =
                    complaintService.searchComplaints(
                            keyword.trim());
        }

        // Check duplicate warning
        checkDuplicateWarnings(complaints);

        model.addAttribute(
                "complaints",
                complaints);

        addStatistics(model);

        return "admindashboard";
    }


    // ==============================
    // FILTER COMPLAINTS
    // ==============================

    @GetMapping("/filter")
    public String filterComplaints(
            @RequestParam("status") String status,
            Model model) {

        List<Complaint> complaints;

        if (status == null
                || status.equals("All")) {

            complaints =
                    complaintService.getAllComplaints();

        } else {

            complaints =
                    complaintService.filterByStatus(
                            status);
        }

        // Check duplicate warning
        checkDuplicateWarnings(complaints);

        model.addAttribute(
                "complaints",
                complaints);

        addStatistics(model);

        return "admindashboard";
    }


    // ==============================
    // DASHBOARD STATISTICS
    // ==============================

    private void addStatistics(Model model) {

        model.addAttribute(
                "totalComplaints",
                complaintService.getTotalComplaints());

        model.addAttribute(
                "pendingComplaints",
                complaintService.getPendingComplaints());

        model.addAttribute(
                "inProgressComplaints",
                complaintService.getInProgressComplaints());

        model.addAttribute(
                "resolvedComplaints",
                complaintService.getResolvedComplaints());
    }


    // ==============================
    // UPDATE COMPLAINT STATUS
    // ==============================

    @PostMapping("/updatestatus")
    public String updateStatus(
            @RequestParam("id") Long id,
            @RequestParam("status") String status) {

        Complaint complaint =
                complaintService.getComplaintById(id);

        if (complaint != null) {

            complaint.setStatus(status);

            complaintService.updateComplaint(
                    complaint);
        }

        return "redirect:/admindashboard";
    }


    // ==============================
    // DELETE COMPLAINT
    // ==============================

    @PostMapping("/deleteComplaint")
    public String deleteComplaint(
            @RequestParam("id") Long id) {

        complaintService.deleteComplaint(id);

        return "redirect:/admindashboard";
    }
}

