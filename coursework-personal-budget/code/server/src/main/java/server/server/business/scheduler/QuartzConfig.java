package server.server.business.scheduler;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz scheduler configuration: schedules {@link DepositMaturityJob} to run daily at 01:00.
 */
@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail depositMaturityJobDetail() {
        return JobBuilder.newJob(DepositMaturityJob.class)
                .withIdentity("depositMaturityJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger depositMaturityTrigger(JobDetail depositMaturityJobDetail) {
        // Fires every day at 01:00
        CronScheduleBuilder schedule = CronScheduleBuilder.cronSchedule("0 0 1 * * ?");
        return TriggerBuilder.newTrigger()
                .forJob(depositMaturityJobDetail)
                .withIdentity("depositMaturityTrigger")
                .withSchedule(schedule)
                .build();
    }
}
