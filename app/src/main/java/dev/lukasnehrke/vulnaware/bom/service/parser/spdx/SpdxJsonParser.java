package dev.lukasnehrke.vulnaware.bom.service.parser.spdx;

import dev.lukasnehrke.vulnaware.bom.model.DataFormat;
import dev.lukasnehrke.vulnaware.bom.service.parser.ParserException;
import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.jacksonstore.MultiFormatStore.Format;
import org.spdx.storage.ISerializableModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.springframework.web.multipart.MultipartFile;

public class SpdxJsonParser extends SpdxParser {

    @Override
    public boolean canHandle(final MultipartFile file) {
        return (
            "application/spdx+json".equals(file.getContentType()) ||
            (file.getOriginalFilename() != null && file.getOriginalFilename().endsWith(".spdx.json"))
        );
    }

    @Override
    protected DataFormat getDataFormat() {
        return DataFormat.JSON;
    }

    @Override
    protected ISerializableModelStore createStore() {
        return new MultiFormatStore(new InMemSpdxStore(), Format.JSON);
    }
}
