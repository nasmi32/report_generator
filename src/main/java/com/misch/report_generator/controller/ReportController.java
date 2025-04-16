package com.misch.report_generator.controller;

import com.misch.report_generator.service.PdfGeneratorService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
public class ReportController {
    private final PdfGeneratorService pdfService;

    public ReportController(PdfGeneratorService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping(value = "/")
    public String home() {
        return "Markdown to PDF";
    }

    // Новый GET-эндпоинт для отображения формы
    @GetMapping(value = "/form", produces = MediaType.TEXT_HTML_VALUE)
    public String showForm() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>PDF Generator</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    textarea { width: 100%; height: 200px; }
                    button { padding: 10px 20px; margin-top: 10px; }
                </style>
            </head>
            <body>
                <h1>Генератор PDF</h1>
                <form action="/convert" method="post" enctype="text/plain">
                    <textarea name="text" placeholder="Введите текст для PDF..."></textarea>
                    <br>
                    <button type="submit">Сгенерировать PDF</button>
                </form>
            </body>
            </html>
            """;
    }

    @PostMapping(value = "/convert", consumes = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> convertToPdf(@RequestBody String text) {
        try {
            String cleanText = text.startsWith("text=") ? text.substring(5) : text;

            byte[] pdf = pdfService.generatePdf(cleanText);
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