package com.renthub.storage;

import com.renthub.common.exception.BadRequestException;
import org.springframework.web.multipart.MultipartFile;
import java.util.Set;

public interface StorageService {
    Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    // Store file, return by public URL
    String store(MultipartFile file);
    void delete(String url);
    static void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File rỗng");
        }
        if (file.getContentType() == null || !ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Chỉ chấp nhận ảnh JPG, PNG hoặc WEBP");
        }
    }
}