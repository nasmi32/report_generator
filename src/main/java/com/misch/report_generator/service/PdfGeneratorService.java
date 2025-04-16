package com.misch.report_generator.service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class PdfGeneratorService {
    private final Parser markdownParser;
    private final HtmlRenderer htmlRenderer;
    private final BaseFont russianFont;

    public PdfGeneratorService() throws IOException, DocumentException {
        this.markdownParser = Parser.builder().build();
        this.htmlRenderer = HtmlRenderer.builder().build();
        this.russianFont = initializeRussianFont();
    }

    private BaseFont initializeRussianFont() throws IOException, DocumentException {
        try {
            // Альтернативный вариант - использовать встроенный шрифт
            return BaseFont.createFont(
                    "fonts/arial.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );
        } catch (Exception e) {
            // Fallback на стандартный шрифт
            return BaseFont.createFont(
                    BaseFont.HELVETICA,
                    BaseFont.WINANSI,
                    BaseFont.EMBEDDED
            );
        }
    }

    public byte[] generatePdfFromMarkdown(String markdown) {
        try {
            Node document = markdownParser.parse(markdown);
            String html = htmlRenderer.render(document);

            com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(pdfDoc, outputStream);

            pdfDoc.open();

            XMLWorkerHelper.getInstance().parseXHtml(
                    writer,
                    pdfDoc,
                    new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)),
                    StandardCharsets.UTF_8,
                    new UnicodeFontProvider(russianFont)
            );

            pdfDoc.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private static class UnicodeFontProvider extends XMLWorkerFontProvider {
        private final BaseFont baseFont;

        public UnicodeFontProvider(BaseFont baseFont) {
            this.baseFont = baseFont;
        }

        @Override
        public Font getFont(String fontname, String encoding,
                            boolean embedded, float size, int style) {
            return new Font(baseFont, size, style);
        }
    }
}