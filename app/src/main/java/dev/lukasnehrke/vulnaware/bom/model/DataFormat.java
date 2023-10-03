package dev.lukasnehrke.vulnaware.bom.model;

import lombok.Getter;

@Getter
public enum DataFormat {
    JSON("json"),
    XML("xml"),
    TAG("tag");

    private final String extension;

    DataFormat(final String extension) {
        this.extension = extension;
    }
}
