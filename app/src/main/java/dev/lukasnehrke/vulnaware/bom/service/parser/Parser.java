package dev.lukasnehrke.vulnaware.bom.service.parser;

import dev.lukasnehrke.vulnaware.bom.model.Bom;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface Parser {
    /**
     * Checks if the file can be handled by this type of parser..
     *
     * @param file The SBOM file to parse.
     * @return The parsed {@link Bom} or {@link Optional#empty()} if the parser cannot handle this file type.
     * @throws ParserException If the file cannot be parsed.
     */
    boolean canHandle(MultipartFile file) throws ParserException;

    /**
     * Transforms a {@link MultipartFile} into the internal {@link Bom} representation.
     *
     * @param file The SBOM file to parse.
     * @return The parsed {@link Bom} or {@link Optional#empty()} if the parser cannot handle this file type.
     * @throws ParserException If the file cannot be parsed.
     */
    Bom parse(MultipartFile file) throws ParserException;
}
