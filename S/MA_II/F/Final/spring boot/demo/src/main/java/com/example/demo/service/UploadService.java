package com.example.demo.service;

import com.example.demo.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class UploadService {

    private static final Set<String> ALLOWED_FOLDERS = Set.of("products", "brands", "avatars");
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp", "gif");

    @Value("${app.uploads-dir:uploads}")
    private String uploadsDir;

    public String store(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("No file uploaded", HttpStatus.BAD_REQUEST);
        }
        String safeFolder = normalizeFolder(folder);
        String ext = extensionOf(file.getOriginalFilename());
        if (!ALLOWED_EXT.contains(ext)) {
            throw new ApiException("Only jpg, jpeg, png, webp, gif are allowed", HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new ApiException("File must be an image", HttpStatus.BAD_REQUEST);
        }

        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path dir = Paths.get(uploadsDir).toAbsolutePath().normalize().resolve(safeFolder);
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ApiException("Failed to save upload", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String encoded = UriUtils.encodePath(fileName, StandardCharsets.UTF_8);
        return "/uploads/" + safeFolder + "/" + encoded;
    }

    private String normalizeFolder(String folder) {
        String value = folder == null || folder.isBlank() ? "products" : folder.trim().toLowerCase(Locale.ROOT);
        if (!ALLOWED_FOLDERS.contains(value)) {
            throw new ApiException("Invalid upload folder", HttpStatus.BAD_REQUEST);
        }
        return value;
    }

    private String extensionOf(String original) {
        if (original == null || !original.contains(".")) {
            throw new ApiException("File must have an extension", HttpStatus.BAD_REQUEST);
        }
        String ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (ext.isBlank()) {
            throw new ApiException("File must have an extension", HttpStatus.BAD_REQUEST);
        }
        return ext;
    }
}
