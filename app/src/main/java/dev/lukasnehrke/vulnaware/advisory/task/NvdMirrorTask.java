package dev.lukasnehrke.vulnaware.advisory.task;

import dev.lukasnehrke.vulnaware.advisory.service.NvdMirrorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("default && !web")
@RequiredArgsConstructor
class NvdMirrorTask {

    private static final Logger logger = LoggerFactory.getLogger(NvdMirrorTask.class);
    private final NvdMirrorService service;

    @Scheduled(initialDelay = 5000, fixedDelay = 1000 * 60 * 60 * 24)
    void sync() throws InterruptedException {
        logger.info("Synchronizing the NVD database..");
        while (true) {
            final boolean hasNext = this.service.sync();
            if (!hasNext) break;
            Thread.sleep(6000);
        }
        logger.info("NVD synchronization completed.");
    }
}
