package dev.lukasnehrke.vulnaware.util;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PageEntity<T> {

    private final long totalElements;
    private final int totalPages;
    private final int page;
    private final List<T> data;
}
