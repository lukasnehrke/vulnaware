package dev.lukasnehrke.vulnaware.storage.service;

import dev.lukasnehrke.vulnaware.storage.error.StorageException;
import java.io.InputStream;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void store(MultipartFile file, String id) throws StorageException;

    void store(InputStream file, String id) throws StorageException;

    Resource loadResource(String id);
}
