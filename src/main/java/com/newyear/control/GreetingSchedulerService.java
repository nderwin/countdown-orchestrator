package com.newyear.control;

import com.newyear.entity.ScheduledGreeting;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

@ApplicationScoped
public class GreetingSchedulerService {
    
    private static final Logger LOG = Logger.getLogger(GreetingSchedulerService.class.getName());
    
    @Inject
    Scheduler quartzScheduler;
    
    @Transactional
    public ScheduledGreeting scheduleGreeting(final GreetingRequest request) {
        final ZoneId recipientZone = ZoneId.of(request.recipientTimezone());
        
        ZonedDateTime deliveryTime;
        
        if (request.testMode()) {
            deliveryTime = ZonedDateTime.now(recipientZone).plusSeconds(5L);
        } else {
            final ZonedDateTime recipientNow = ZonedDateTime.now(recipientZone);
            int recipientYear = recipientNow.getYear();
            // if the recipient has already had the new year, then bump to their next new year
            int targetYear = recipientNow.getMonthValue() > 1 || (1 == recipientNow.getMonthValue() && recipientNow.getDayOfMonth() > 1)
                    ? recipientYear + 1
                    : recipientYear;
            deliveryTime = ZonedDateTime.of(targetYear, 1, 1, 0, 0, 0, 0, recipientZone);
        }
        
        final ScheduledGreeting greeting = new ScheduledGreeting(
                request.senderName(), 
                request.recipientName(), 
                request.recipientTimezone(), 
                deliveryTime, 
                request.message()
        );
        greeting.setDeliveryChannel(request.deliveryChannel());
        greeting.setContactInfo(request.contactInfo());
        
        greeting.persist();
        
        scheduleQuartzJob(greeting);
        
        return greeting;
    }
    
    private void scheduleQuartzJob(final ScheduledGreeting greeting) {
        try {
            final String jobId = "greeting-" + greeting.id;
            
            final JobDetail job = JobBuilder.newJob(GreetingDeliveryJob.class)
                    .withIdentity(jobId, "greetings")
                    .usingJobData("greetingId", "" + greeting.id)
                    .build();
            
            final Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger-" + jobId, "greetings")
                    .startAt(Date.from(greeting.getTargetDeliveryTime().toInstant()))
                    .build();
            
            quartzScheduler.scheduleJob(job, trigger);
            
            greeting.setQuartzJobId(jobId);
            
            LOG.log(Level.INFO, "Scheduled greeting {0} for {1} at {2}", new Object[]{
                greeting.id, 
                greeting.getRecipientName(), 
                greeting.getTargetDeliveryTime()
            });
        } catch (final SchedulerException ex) {
            throw new RuntimeException("Scheduling failed", ex);
        }
    }
}
