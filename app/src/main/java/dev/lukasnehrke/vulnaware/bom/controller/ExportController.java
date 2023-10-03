package dev.lukasnehrke.vulnaware.bom.controller;

import dev.lukasnehrke.vulnaware.bom.service.ExportService;
import dev.lukasnehrke.vulnaware.project.service.ProjectService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectSlug}/bom/{tag}")
@RequiredArgsConstructor
class ExportController {

    private final ProjectService projectService;
    private final ExportService exportService;

    @GetMapping("/export/cyclonedx")
    ResponseEntity<?> exportCycloneDx(
        @PathVariable final String projectSlug,
        @PathVariable final String tag,
        @RequestParam(defaultValue = "true") final boolean includeVulns
    ) throws IOException {
        final var project = projectService.findBySlug(projectSlug);
        final var json = exportService.exportToCycloneDx(project, tag, includeVulns);

        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDisposition(ContentDisposition.attachment().filename(tag + ".cdx.json").build());
        return new ResponseEntity<>(json, headers, 200);
    }

    @GetMapping("/export/spdx")
    ResponseEntity<?> exportSpdx(
        @PathVariable final String projectSlug,
        @PathVariable final String tag,
        @RequestParam(defaultValue = "true") final boolean includeVulns
    ) throws IOException {
        final var project = projectService.findBySlug(projectSlug);
        final var json = exportService.exportToSpdx(project, tag, includeVulns);

        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDisposition(ContentDisposition.attachment().filename(tag + ".spdx.json").build());
        return new ResponseEntity<>(json, headers, 200);
    }

    @GetMapping("/export/csv")
    void exportCSV(@PathVariable final String projectSlug, @PathVariable final String tag, final HttpServletResponse response) throws Exception {
        response.setStatus(200);
        response.setHeader("Content-Type", "text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"components.csv\"");

        final var project = projectService.findBySlug(projectSlug);
        exportService.exportToCsv(project, tag, response.getWriter());
    }
}
