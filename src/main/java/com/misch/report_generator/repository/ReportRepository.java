package com.misch.report_generator.repository;

import com.misch.report_generator.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
    List<Report> findAllByOrderByCreatedAtDesc();

    Optional<Report> findByGeneratedFilename(String generatedFilename);
}