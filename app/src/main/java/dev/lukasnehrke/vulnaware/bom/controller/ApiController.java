package dev.lukasnehrke.vulnaware.bom.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.annotation.JsonView;
import dev.lukasnehrke.vulnaware.bom.dto.ApiVulnResponse;
import dev.lukasnehrke.vulnaware.bom.model.Bom;
import dev.lukasnehrke.vulnaware.bom.service.BomService;
import dev.lukasnehrke.vulnaware.project.model.Project;
import dev.lukasnehrke.vulnaware.security.UserDetailsImpl;
import dev.lukasnehrke.vulnaware.user.CurrentUser;
import dev.lukasnehrke.vulnaware.vulnerability.service.VulnerabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/v1/{project}")
@RestController
@RequiredArgsConstructor
class ApiController {

    private final BomService bomService;
    private final VulnerabilityService vulnService;

    @PostMapping(value = "/upload/{tag}", produces = APPLICATION_JSON_VALUE)
    @JsonView(Bom.PublicView.class)
    ResponseEntity<?> uploadBom(
        @CurrentUser final UserDetailsImpl userDetails,
        @PathVariable final Project project,
        @PathVariable final String tag,
        @Nullable @RequestParam("description") final String description,
        @RequestParam("file") final MultipartFile file
    ) {
        final var bom = bomService.createBom(userDetails, project, tag, file, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(bom);
    }

    @PostMapping(value = "/analyze/{tag}", produces = APPLICATION_JSON_VALUE)
    @JsonView(Bom.PublicView.class)
    ResponseEntity<?> analyzeBom(
        @CurrentUser final UserDetailsImpl userDetails,
        @PathVariable final Project project,
        @PathVariable final String tag,
        @Nullable @RequestParam("description") final String description,
        @RequestParam("file") final MultipartFile file
    ) {
        final var bom = bomService.createBom(userDetails, project, tag, file, description);
        final var response = vulnService.analyze(bom.getId()).stream().map(v -> new ApiVulnResponse(v.getAdvisory().getId())).toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
