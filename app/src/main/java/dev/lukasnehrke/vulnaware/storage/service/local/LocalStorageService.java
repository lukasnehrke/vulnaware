package dev.lukasnehrke.vulnaware.storage.service.local;

import dev.lukasnehrke.vulnaware.storage.error.StorageException;
import dev.lukasnehrke.vulnaware.storage.service.StorageService;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(name = "vulnaware.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalStorageService.class);
    private final Path root;

    LocalStorageService() {
        this.root = Paths.get("./storage").normalize().toAbsolutePath();
        logger.info("Storing files at '{}'", this.root);
    }

    @Override
    public void store(final MultipartFile file, final String id) {
        try {
            final var target = this.root.resolve(Paths.get(id)).normalize().toAbsolutePath();
            if (!target.startsWith(root)) throw new StorageException("Detected Path Traversal");

            try (InputStream inputStream = file.getInputStream()) {
                Files.createDirectories(target.getParent());
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public void store(final InputStream file, final String id) throws StorageException {
        try {
            final var target = this.root.resolve(Paths.get(id)).normalize().toAbsolutePath();
            if (!target.startsWith(root)) throw new StorageException("Detected Path Traversal");
            Files.createDirectories(target.getParent());
            Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public Resource loadResource(final String id) {
        try {
            final Path path = root.resolve(id);
            final Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() && !resource.isReadable()) {
                throw new StorageException("Cannot read file: " + id);
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new StorageException("Cannot read file: " + id, ex);
        }
    }
}
