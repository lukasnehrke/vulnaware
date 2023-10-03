package dev.lukasnehrke.vulnaware.bom.service.parser.spdx;

import dev.lukasnehrke.vulnaware.bom.model.DataFormat;
import org.spdx.storage.ISerializableModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.tagvaluestore.TagValueStore;
import org.springframework.web.multipart.MultipartFile;

public class SpdxTagParser extends SpdxParser {

    @Override
    public boolean canHandle(final MultipartFile file) {
        return file.getOriginalFilename() != null && file.getOriginalFilename().endsWith(".spdx");
    }

    @Override
    protected DataFormat getDataFormat() {
        return DataFormat.TAG;
    }

    @Override
    protected ISerializableModelStore createStore() {
        return new TagValueStore(new InMemSpdxStore());
    }
}
