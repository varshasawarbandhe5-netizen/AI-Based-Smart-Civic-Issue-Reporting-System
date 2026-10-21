package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Complaint;
import com.example.demo.service.ComplaintService;
import com.example.demo.service.ImageAiService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ReportController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private ImageAiService imageAiService;


    // =========================================================
    // TEST CONTROLLER
    // =========================================================

    @GetMapping("/test")
    public String test() {

        System.out.println(
                "TEST CONTROLLER WORKING"
        );

        return "redirect:/report";
    }


    // =========================================================
    // REPORT PAGE
    // =========================================================

    @GetMapping("/report")
    public String reportPage() {

        return "report_issue";
    }


    // =========================================================
    // SAVE COMPLAINT
    // =========================================================

    @PostMapping("/report")
    public String saveComplaint(

            @RequestParam("title") String title,

            @RequestParam("description") String description,

            @RequestParam("category") String category,

            @RequestParam("location") String location,

            @RequestParam("imageFile") MultipartFile imageFile,

            HttpSession session) {


        System.out.println(
                "Image Empty = "
                + imageFile.isEmpty()
        );


        System.out.println(
                "Image Name = "
                + imageFile.getOriginalFilename()
        );


        // =====================================================
        // GET LOGGED-IN CITIZEN EMAIL
        // =====================================================

        String citizenEmail =
                (String) session.getAttribute(
                        "loggedInUserEmail"
                );


        System.out.println(
                "Logged-in Citizen = "
                + citizenEmail
        );


        // =====================================================
        // CREATE COMPLAINT
        // =====================================================

        Complaint complaint =
                new Complaint();


        complaint.setTitle(
                title
        );


        complaint.setDescription(
                description
        );


        complaint.setCategory(
                category
        );


        complaint.setLocation(
                location
        );


        complaint.setStatus(
                "Pending"
        );


        // =====================================================
        // SAVE CITIZEN EMAIL
        // =====================================================

        complaint.setCitizenEmail(
                citizenEmail
        );


        // =====================================================
        // AI COMPLAINT SUMMARY
        // =====================================================

        try {

            String aiSummary =
                    imageAiService.generateSummary(
                            description
                    );


            complaint.setAiSummary(
                    aiSummary
            );


            System.out.println(
                    "AI Summary = "
                    + aiSummary
            );

        } catch (Exception e) {

            System.out.println(
                    "AI Summary Error"
            );

            e.printStackTrace();


            // Keep original description safe
            complaint.setAiSummary(
                    description
            );
        }


        // =====================================================
        // IMAGE UPLOAD + AI IMAGE CATEGORY
        // =====================================================

        if (!imageFile.isEmpty()) {

            try {

                // =================================================
                // IMPORTANT:
                // READ IMAGE BYTES BEFORE transferTo()
                // =================================================

                byte[] imageBytes =
                        imageFile.getBytes();


                String mimeType =
                        imageFile.getContentType();


                // =================================================
                // UPLOAD DIRECTORY
                // =================================================

                String uploadDir =
                        System.getProperty("user.dir")
                        + File.separator
                        + "uploads"
                        + File.separator;


                System.out.println(
                        "Upload Path = "
                        + uploadDir
                );


                File folder =
                        new File(
                                uploadDir
                        );


                if (!folder.exists()) {

                    folder.mkdirs();
                }


                // =================================================
                // FILE NAME
                // =================================================

                String fileName =
                        imageFile.getOriginalFilename();


                File destination =
                        new File(
                                uploadDir
                                + fileName
                        );


                // =================================================
                // SAVE IMAGE
                // =================================================

                imageFile.transferTo(
                        destination
                );


                complaint.setImage(
                        fileName
                );


                System.out.println(
                        "Saved Image = "
                        + complaint.getImage()
                );


                // =================================================
                // AI IMAGE CATEGORY
                // =================================================

                try {

                    String aiCategory =
                            imageAiService.detectCategory(
                                    imageBytes,
                                    mimeType
                            );


                    complaint.setAiCategory(
                            aiCategory
                    );


                    System.out.println(
                            "AI Category = "
                            + aiCategory
                    );

                } catch (Exception e) {

                    System.out.println(
                            "AI Image Category Error"
                    );

                    e.printStackTrace();


                    complaint.setAiCategory(
                            "Other"
                    );
                }


            } catch (IOException e) {

                System.out.println(
                        "Image Upload Error"
                );

                e.printStackTrace();
            }
        }


        // =====================================================
        // SAVE COMPLAINT
        // =====================================================

        complaintService.saveComplaint(
                complaint
        );


        System.out.println(
                "Complaint Saved Successfully"
        );


        // =====================================================
        // REDIRECT
        // =====================================================

        return "redirect:/dashboard";
    }


    // =========================================================
    // SHOW UPLOADED IMAGE
    // =========================================================

    @GetMapping("/uploads/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(
            @PathVariable String fileName)
            throws Exception {


        Path path =
                Paths.get("uploads")
                .resolve(fileName);


        Resource resource =
                new UrlResource(
                        path.toUri()
                );


        // =====================================================
        // CHECK IMAGE
        // =====================================================

        if (!resource.exists()
                || !resource.isReadable()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        // =====================================================
        // DETECT IMAGE TYPE
        // =====================================================

        String contentType =
                Files.probeContentType(
                        path
                );


        if (contentType == null) {

            contentType =
                    "application/octet-stream";
        }


        // =====================================================
        // RETURN IMAGE
        // =====================================================

        return ResponseEntity
                .ok()
                .contentType(
                        MediaType.parseMediaType(
                                contentType
                        )
                )
                .body(resource);
    }
}