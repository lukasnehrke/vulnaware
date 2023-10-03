package dev.lukasnehrke.vulnaware.advisory.task;

import dev.lukasnehrke.vulnaware.advisory.service.OsvMirrorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("default && !web")
@RequiredArgsConstructor
class OsvMirrorTask {

    private static final Logger logger = LoggerFactory.getLogger(OsvMirrorTask.class);
    private final OsvMirrorService osvMirrorService;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    void sync() throws Exception {
        logger.info("Synchronizing the OSV database..");
        for (final String ecosystem : osvMirrorService.getEcosystems()) {
            osvMirrorService.syncEcosystem(ecosystem);
        }
    }
}
