package com.renthub.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {
    private final Path root;
    private final String publicUrl;

    public LocalStorageService(
            @Value("${app.storage.upload-dir}") String uploadDir,
            @Value("${app.storage.public-url}") String publicUrl
    ) throws IOException {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(root);
        this.publicUrl = publicUrl.replaceAll("/+$", "");
    }

    @Override
    public String store(MultipartFile file) {
        StorageService.validateImage(file);
        String ext = switch (file.getContentType()) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
        String filename = UUID.randomUUID() + ext;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, root.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Không lưu được file", e);
        }
        return publicUrl + "/uploads/" + filename;
    }

    @Override
    public void delete(String url) {
        String prefix = publicUrl + "/uploads/";
        if (url == null || !url.startsWith(prefix)) return;
        try {
            Files.deleteIfExists(root.resolve(url.substring(prefix.length())).normalize());
        } catch (IOException ignored) {}
    }
}