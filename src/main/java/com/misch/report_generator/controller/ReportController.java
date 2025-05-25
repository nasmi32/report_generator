package com.misch.report_generator.controller;

import com.misch.report_generator.model.Report;
import com.misch.report_generator.repository.ReportRepository;
import com.misch.report_generator.service.ReportService;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@RestController
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("mdFile") MultipartFile mdFile,
                                              @RequestParam(value = "zipFile", required = false) MultipartFile zipFile) {
        try {
            String fileName = reportService.generatePdfFromMarkdown(mdFile, zipFile);
            return ResponseEntity.ok(fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable String filename) {
        File file = reportService.getDownloadedFile(filename);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.APPLICATION_PDF)
                .body(new FileSystemResource(file));
    }

    @GetMapping("/reports")
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<Report> getReportInfo(@PathVariable UUID id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @GetMapping("/reports/history")
    public String getHistory(Model model) {
        model.addAttribute("reports", reportService.getAllReports());
        return "history"; // шаблон Thymeleaf
    }

    // Скачивание старого отчёта
    @GetMapping("/reports/download-old/{id}")
    public ResponseEntity<FileSystemResource> downloadOldReport(@PathVariable UUID id) {
        Report report = reportService.getReportById(id);
        File file = reportService.getDownloadedFile(report.getGeneratedFilename());

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.APPLICATION_PDF)
                .body(new FileSystemResource(file));
    }
}