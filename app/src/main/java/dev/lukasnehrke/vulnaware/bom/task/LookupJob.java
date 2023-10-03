package dev.lukasnehrke.vulnaware.bom.task;

import dev.lukasnehrke.vulnaware.bom.service.LookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("lookup-job")
@RequiredArgsConstructor
class LookupJob {

    private final LookupService lookupService;

    @Bean
    public CommandLineRunner sync() {
        return args -> lookupService.lookupLatest();
    }
}
