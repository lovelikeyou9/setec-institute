package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif", "image/avif", "image/svg+xml"
    );

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String storeImage(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) return null;
        String contentType = file.getContentType() != null ? file.getContentType().toLowerCase(Locale.ENGLISH) : "";
        if (!contentType.startsWith("image/") && !ALLOWED.contains(contentType)) {
            throw new RuntimeException("Please upload an image file (PNG, JPG, WEBP, GIF, or SVG)");
        }
        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "logo.png";
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) ext = original.substring(dot).toLowerCase(Locale.ENGLISH);
        if (ext.isBlank() || ext.length() > 8) {
            ext = contentType.contains("png") ? ".png"
                    : contentType.contains("webp") ? ".webp"
                    : contentType.contains("gif") ? ".gif"
                    : contentType.contains("svg") ? ".svg"
                    : ".jpg";
        }
        String filename = UUID.randomUUID().toString().replace("-", "").substring(0, 16) + ext;
        try {
            Path dir = Paths.get(uploadDir, subfolder).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + subfolder + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Could not save image: " + e.getMessage());
        }
    }
}
