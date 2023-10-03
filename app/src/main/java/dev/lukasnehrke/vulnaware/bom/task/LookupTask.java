package dev.lukasnehrke.vulnaware.bom.task;

import dev.lukasnehrke.vulnaware.bom.service.LookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("default && !web")
@RequiredArgsConstructor
class LookupTask {

    private final LookupService lookupService;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    public void lookup() {
        lookupService.lookupLatest();
    }
}
