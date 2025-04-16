package com.misch.report_generator.controller;

import com.misch.report_generator.service.PdfGeneratorService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@Controller
public class ReportController {
    private final PdfGeneratorService pdfService;

    public ReportController(PdfGeneratorService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/")
    public String home() {
        return "form";
    }

    @GetMapping("/form")
    public String showForm() {
        return "form"; // Имя файла без .html
    }

    @PostMapping(value = "/convert", consumes = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> convertToPdf(@RequestBody String markdownText) {
        try {
            byte[] pdf = pdfService.generatePdfFromMarkdown(markdownText);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=document.pdf")
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(("Ошибка генерации PDF: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }
}