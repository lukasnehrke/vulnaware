package dev.lukasnehrke.vulnaware.storage.service;

import dev.lukasnehrke.vulnaware.storage.model.Asset;
import dev.lukasnehrke.vulnaware.storage.repository.AssetRepository;
import jakarta.transaction.Transactional;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assets;
    private final StorageService storageService;

    @Transactional
    public Asset createAsset(final String location, final MultipartFile file) {
        storageService.store(file, location);
        return createAsset(location);
    }

    @Transactional
    public Asset createAsset(final String location, final InputStream file) {
        storageService.store(file, location);
        return createAsset(location);
    }

    private Asset createAsset(String location) {
        final Asset asset = assets.findByLocation(location).orElse(new Asset());
        asset.setLocation(location);
        return assets.save(asset);
    }
}
