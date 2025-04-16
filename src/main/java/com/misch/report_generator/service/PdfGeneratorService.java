package com.misch.report_generator.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfGeneratorService {

    public byte[] generatePdf(String text) throws DocumentException, IOException {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be empty");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter writer = null;

        try {
            // Настройка шрифта для поддержки русского текста
            BaseFont bf = BaseFont.createFont(
                    BaseFont.HELVETICA,
                    BaseFont.CP1250,
                    BaseFont.EMBEDDED
            );
            Font font = new Font(bf, 12);

            writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            document.add(new Paragraph(text, font));

            // Важно закрыть документ перед получением данных
            document.close();
            writer.close();

            return outputStream.toByteArray();
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }
}