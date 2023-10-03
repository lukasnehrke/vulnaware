package dev.lukasnehrke.vulnaware.storage.service.remote;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import dev.lukasnehrke.vulnaware.storage.error.StorageException;
import dev.lukasnehrke.vulnaware.storage.service.StorageService;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of {@link StorageService} that stores data in Google Cloud Storage.
 */
@Service
@ConditionalOnProperty(name = "vulnaware.storage.type", havingValue = "gcs")
public class CloudStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(CloudStorageService.class);

    @Value("${vulnaware.storage.gcs.project}")
    private String projectId;

    @Value("${vulnaware.storage.gcs.bucket}")
    private String bucket;

    private Storage storage;

    @Override
    public void store(final MultipartFile file, final String id) throws StorageException {
        try {
            final BlobId blobId = BlobId.of(bucket, id);
            final BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

            storage.createFrom(blobInfo, file.getInputStream());
            logger.info("Uploaded file '{}' to bucket {}", id, bucket);
        } catch (final IOException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public void store(InputStream file, String id) throws StorageException {
        try {
            final BlobId blobId = BlobId.of(bucket, id);
            final BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

            storage.createFrom(blobInfo, file);
            logger.info("Uploaded file '{}' to bucket {}", id, bucket);
        } catch (final IOException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public Resource loadResource(final String id) {
        return new ByteArrayResource(storage.readAllBytes(bucket, id));
    }

    @PostConstruct
    void createStorage() {
        checkNotNull(projectId, "Project ID must not be null");
        checkNotNull(bucket, "Bucket must not be null");

        storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    }
}
