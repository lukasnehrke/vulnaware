package dev.lukasnehrke.vulnaware.advisory.task;

import dev.lukasnehrke.vulnaware.advisory.service.OsvMirrorService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("osv-mirror-job")
@RequiredArgsConstructor
class OsvMirrorJob {

    private final OsvMirrorService mirrorService;

    @Bean
    public CommandLineRunner sync() {
        return args -> {
            for (final String ecosystem : mirrorService.getEcosystems()) {
                mirrorService.syncEcosystem(ecosystem);
            }
        };
    }
}
