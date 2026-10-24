package server.server.business.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import server.server.business.service.finance.DepositMaturityService;

import java.time.LocalDate;

/**
 * Quartz job executed daily at 01:00.
 * Delegates all logic to {@link DepositMaturityService} so the same
 * processing can also run at server startup (see {@link DepositStartupHandler}).
 */
@Slf4j
@Component
public class DepositMaturityJob implements Job {

    private final DepositMaturityService depositMaturityService;

    public DepositMaturityJob(DepositMaturityService depositMaturityService) {
        this.depositMaturityService = depositMaturityService;
    }

    @Override
    public void execute(JobExecutionContext context) {
        log.info("DepositMaturityJob: running scheduled check as of {}", LocalDate.now());
        int processed = depositMaturityService.processMatureDeposits();
        log.info("DepositMaturityJob: {} deposit(s) closed", processed);
    }
}
