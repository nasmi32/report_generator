package com.misch.report_generator.controller;

import com.misch.report_generator.service.PdfGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class ReportController {
    private final PdfGeneratorService pdfService;

    public ReportController(PdfGeneratorService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/convert")
    public ResponseEntity<byte[]> convertToPdf(@RequestBody String text) {
        try {
            byte[] pdf = pdfService.generatePdf(text);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=report.pdf")
                    .body(pdf);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(("Пффф... " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Ошибка генерации PDF: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }
}