package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Complaint;
import com.example.demo.repository.ComplaintRepository;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    // ==============================
    // SAVE COMPLAINT
    // ==============================

    public Complaint saveComplaint(Complaint complaint) {

        String text = (
                complaint.getTitle() + " "
                + complaint.getDescription()
        ).toLowerCase();

        // ==============================
        // AI CATEGORY FALLBACK
        // ==============================

        if (complaint.getAiCategory() == null
                || complaint.getAiCategory().trim().isEmpty()
                || complaint.getAiCategory().equals("Other")) {

            // Drainage FIRST
            if (text.contains("drainage")
                    || text.contains("drain")
                    || text.contains("gutter")
                    || text.contains("sewer")) {

                complaint.setAiCategory("Drainage");

            } else if (text.contains("street light")
                    || text.contains("streetlight")) {

                complaint.setAiCategory("Street Light");

            } else if (text.contains("garbage")
                    || text.contains("waste")
                    || text.contains("trash")) {

                complaint.setAiCategory("Garbage Issue");

            } else if (text.contains("water leakage")
                    || text.contains("water leak")
                    || text.contains("leaking pipe")
                    || text.contains("broken water pipe")
                    || text.contains("water supply pipe")) {

                complaint.setAiCategory("Water Leakage");

            } else if (text.contains("road")
                    || text.contains("pothole")) {

                complaint.setAiCategory("Road Issue");

            } else if (text.contains("traffic signal")
                    || text.contains("traffic light")
                    || text.contains("signal")) {

                complaint.setAiCategory("Traffic Signal");

            } else {

                complaint.setAiCategory("Other");
            }
        }

        // ==============================
        // AI PRIORITY
        // ==============================

        // HIGH PRIORITY
        if (text.contains("accident")
                || text.contains("fire")
                || text.contains("dangerous")
                || text.contains("danger")
                || text.contains("emergency")
                || text.contains("life threat")
                || text.contains("urgent")) {

            complaint.setAiPriority("High");

        }

        // MEDIUM PRIORITY
        else if (complaint.getAiCategory().equals("Garbage Issue")
                || complaint.getAiCategory().equals("Water Leakage")
                || complaint.getAiCategory().equals("Drainage")
                || text.contains("garbage")
                || text.contains("waste")
                || text.contains("trash")
                || text.contains("water leakage")
                || text.contains("leakage")
                || text.contains("drainage")
                || text.contains("drain")) {

            complaint.setAiPriority("Medium");

        }

        // LOW PRIORITY
        else if (complaint.getAiCategory().equals("Street Light")
                || complaint.getAiCategory().equals("Road Issue")
                || complaint.getAiCategory().equals("Traffic Signal")
                || text.contains("minor road issue")
                || text.contains("small pothole")) {

            complaint.setAiPriority("Low");

        }

        // DEFAULT
        else {

            complaint.setAiPriority("Low");
        }

        // ==============================
        // AI DEPARTMENT
        // ==============================

        String aiCategory = complaint.getAiCategory();

        if (aiCategory != null) {

            if (aiCategory.equals("Road Issue")) {

                complaint.setAiDepartment(
                        "Road Department");

            } else if (aiCategory.equals("Garbage Issue")) {

                complaint.setAiDepartment(
                        "Sanitation Department");

            } else if (aiCategory.equals("Water Leakage")) {

                complaint.setAiDepartment(
                        "Water Supply Department");

            } else if (aiCategory.equals("Drainage")) {

                complaint.setAiDepartment(
                        "Drainage Department");

            } else if (aiCategory.equals("Street Light")) {

                complaint.setAiDepartment(
                        "Electrical Department");

            } else if (aiCategory.equals("Traffic Signal")) {

                complaint.setAiDepartment(
                        "Traffic Department");

            } else {

                complaint.setAiDepartment(
                        "General Department");
            }
        }

        // ==============================
        // PRINT AI RESULT
        // ==============================

        System.out.println(
                "AI Category = "
                + complaint.getAiCategory());

        System.out.println(
                "AI Priority = "
                + complaint.getAiPriority());

        System.out.println(
                "AI Department = "
                + complaint.getAiDepartment());

        // ==============================
        // SAVE
        // ==============================

        return complaintRepository.save(complaint);
    }

    // ==============================
    // GET ALL
    // ==============================

    public List<Complaint> getAllComplaints() {

        return complaintRepository.findAll();
    }

    // ==============================
    // GET BY ID
    // ==============================

    public Complaint getComplaintById(Long id) {

        return complaintRepository
                .findById(id)
                .orElse(null);
    }

    // ==============================
    // UPDATE
    // ==============================

    public Complaint updateComplaint(
            Complaint complaint) {

        return complaintRepository.save(complaint);
    }

    // ==============================
    // DELETE
    // ==============================

    public void deleteComplaint(Long id) {

        complaintRepository.deleteById(id);
    }

    // ==============================
    // SEARCH
    // ==============================

    public List<Complaint> searchComplaints(
            String keyword) {

        return complaintRepository
                .findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrLocationContainingIgnoreCase(
                        keyword,
                        keyword,
                        keyword
                );
    }

    // ==============================
    // FILTER STATUS
    // ==============================

    public List<Complaint> filterByStatus(
            String status) {

        return complaintRepository
                .findByStatus(status);
    }

    // ==============================
    // TOTAL
    // ==============================

    public long getTotalComplaints() {

        return complaintRepository.count();
    }

    // ==============================
    // PENDING
    // ==============================

    public long getPendingComplaints() {

        return complaintRepository
                .countByStatus("Pending");
    }

    // ==============================
    // IN PROGRESS
    // ==============================

    public long getInProgressComplaints() {

        return complaintRepository
                .countByStatus("In Progress");
    }

    // ==============================
    // RESOLVED
    // ==============================

    public long getResolvedComplaints() {

        return complaintRepository
                .countByStatus("Resolved");
    }
}