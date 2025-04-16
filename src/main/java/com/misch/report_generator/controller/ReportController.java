package com.misch.report_generator.controller;

import com.itextpdf.text.DocumentException;
import com.misch.report_generator.service.PdfGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ReportController {
    private final PdfGeneratorService pdfService;

    // Явное объявление конструктора для внедрения зависимости
    public ReportController(PdfGeneratorService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/convert")
    public ResponseEntity<byte[]> convertToPdf(@RequestBody String text) throws DocumentException, IOException {
        try {
            byte[] pdf = pdfService.generatePdf(text);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .body(pdf);
        } catch (DocumentException | IOException e) {
            return ResponseEntity.badRequest()
                    .body(("Пффф... Ошибка генерации PDF: " + e.getMessage()).getBytes());
        }
    }
}