package com.newyear.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@ApplicationScoped
public class GreetingDeliveryJob implements Job {
    
    @Inject
    GreetingDeliveryService deliveryService;

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        final Long greetingId = context.getJobDetail().getJobDataMap().getLong("greetingId");
        
        try {
            deliveryService.deliver(greetingId);
        } catch (final Exception ex) {
            throw new JobExecutionException(ex);
        }
    }
    
}
