package com.misch.report_generator.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class PdfGeneratorService {
    private final Parser markdownParser = Parser.builder().build();
    private final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
    private final BaseFont russianFont;

    public PdfGeneratorService() throws IOException, DocumentException {
        // Используем шрифт с поддержкой кириллицы, лежащий в resources/fonts
        russianFont = BaseFont.createFont(
                new ClassPathResource("fonts/DejaVuSans.ttf").getPath(),
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED
        );
    }

    public byte[] generatePdfFromMarkdown(String markdown) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Node document = markdownParser.parse(markdown);
            String html = htmlRenderer.render(document);

            Document pdfDoc = new Document();
            PdfWriter writer = PdfWriter.getInstance(pdfDoc, outputStream);
            pdfDoc.open();

            // Парсим HTML + поддержка изображений + кириллица
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
            throw new RuntimeException("Ошибка генерации PDF", e);
        }
    }

    private static class UnicodeFontProvider extends XMLWorkerFontProvider {
        private final BaseFont baseFont;

        public UnicodeFontProvider(BaseFont baseFont) {
            this.baseFont = baseFont;
        }

        @Override
        public Font getFont(String fontname, String encoding,
                            boolean embedded, float size, int style, BaseColor color) {
            return new Font(baseFont, size, style, color);
        }
    }
}