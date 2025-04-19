package com.misch.report_generator.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ReportService {

    private final Path downloadDir = Paths.get("downloads");

    public String generatePdfFromMarkdown(MultipartFile mdFile, MultipartFile zipFile) throws Exception {
        Path tempDir = Files.createTempDirectory("upload_");
        Path mdPath = tempDir.resolve(mdFile.getOriginalFilename());
        Files.write(mdPath, mdFile.getBytes());

        Path imagesDir = null;

        if (zipFile != null && !zipFile.isEmpty()) {
            imagesDir = tempDir.resolve("images");
            Files.createDirectories(imagesDir);
            unzip(zipFile, imagesDir);
        }

        String pdfFileName = UUID.randomUUID() + ".pdf";
        Path pdfPath = tempDir.resolve(pdfFileName);

        List<String> command = new ArrayList<>(List.of(
                "pandoc",
                mdPath.toString(),
                "-o", pdfPath.toString(),
                "--pdf-engine=lualatex",
                "--number-sections",
                "--filter", "pandoc-crossref",
                "-V", "mainfont=FreeSerif",
                "-V", "lang=ru-RU"
        ));

        if (imagesDir != null) {
            command.add("--resource-path=" + imagesDir.toAbsolutePath());
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[pandoc] " + line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Ошибка генерации PDF. Код выхода: " + exitCode);
        }

        Files.createDirectories(downloadDir);
        Path finalPath = downloadDir.resolve(pdfFileName);
        Files.move(pdfPath, finalPath, StandardCopyOption.REPLACE_EXISTING);

        return pdfFileName;
    }

    public File getDownloadedFile(String filename) {
        return downloadDir.resolve(filename).toFile();
    }

    private void unzip(MultipartFile zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path newPath = targetDir.resolve(entry.getName()).normalize();
                if (!newPath.startsWith(targetDir)) {
                    throw new IOException("Bad zip entry: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    Files.createDirectories(newPath.getParent());
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}
