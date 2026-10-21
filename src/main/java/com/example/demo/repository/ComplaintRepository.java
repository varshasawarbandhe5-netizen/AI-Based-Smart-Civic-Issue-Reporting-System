
package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Complaint;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // Count complaints according to status
    long countByStatus(String status);

    // Search complaints
    List<Complaint> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String title,
            String category,
            String location
    );

    // Filter complaints by status
    List<Complaint> findByStatus(String status);

    // Find similar complaints by location and AI detected category
    List<Complaint> findByLocationContainingIgnoreCaseAndAiCategory(
            String location,
            String aiCategory
    );

    // Find complaints of a particular citizen
    List<Complaint> findByCitizenEmail(String citizenEmail);

}

