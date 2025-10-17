package com.metamapa.domain;

/* ---CONFIG DE QUARTZ---
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    // -------- TRAER HECHOS -----------
    @Bean
    public JobDetail traerHechosJobDetail() {
        return JobBuilder.newJob(TraerHechosJob.class)
                .withIdentity("TraerHechosJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger traerHechosTrigger(JobDetail traerHechosJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(traerHechosJobDetail)
                .withIdentity("TraerHechosTrigger")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(1)
                        .repeatForever())
                .build();
    }
}
*/
