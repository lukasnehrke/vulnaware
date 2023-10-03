package dev.lukasnehrke.vulnaware.util.schema.osi;

import java.util.List;

public record QueryResponse(boolean isDefault, List<Link> links, List<String> licenses, String publishedAt) {}
