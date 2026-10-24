package server.server.business.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import server.server.business.service.finance.DepositMaturityService;

/**
 * Runs deposit-maturity processing once immediately after the application
 * has fully started. This ensures that deposits which matured while the
 * server was offline are closed and their income transactions are created
 * without waiting for the next daily Quartz trigger.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DepositStartupHandler {

    private final DepositMaturityService depositMaturityService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("DepositStartupHandler: checking for overdue deposits on startup...");
        int processed = depositMaturityService.processMatureDeposits();
        if (processed > 0) {
            log.info("DepositStartupHandler: closed {} overdue deposit(s) that missed the scheduled job", processed);
        } else {
            log.info("DepositStartupHandler: no overdue deposits found");
        }
    }
}
