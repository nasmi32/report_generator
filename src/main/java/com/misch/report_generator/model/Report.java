package com.misch.report_generator.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Отчёт, сгенерированный из Markdown-файла")
@Entity
@Table(name = "reports")
@Data
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "Уникальный идентификатор отчёта", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Column(name = "original_filename")
    @Schema(description = "Исходное имя файла", example = "example.md")
    private String originalFilename;

    @Column(name = "generated_filename", unique = true)
    @Schema(description = "Имя сгенерированного файла", example = "report_2025.pdf")
    private String generatedFilename;

    @Column(name = "created_at")
    @Schema(description = "Дата создания отчёта", example = "2025-05-25T12:00:00")
    private LocalDateTime createdAt;

    @Column(name = "file_size")
    @Schema(description = "Размер файла в байтах", example = "2048")
    private Long fileSize;

    @Column(name = "status")
    @Schema(description = "Статус отчёта", example = "GENERATED")
    private String status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = "GENERATED";
    }
}