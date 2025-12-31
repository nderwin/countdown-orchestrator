package com.newyear.control;

import com.newyear.boundary.GlobeWebSocket;
import com.newyear.entity.ScheduledGreeting;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class GreetingDeliveryService {
    
    private static final Logger LOG = Logger.getLogger(GreetingDeliveryService.class.getName());
    
    @Inject
    GlobeWebSocket wsNotifier;
    
    @Transactional
    public void deliver(final Long greetingId) {
        final ScheduledGreeting greeting = ScheduledGreeting.findById(greetingId);
        
        if (null == greeting) {
            LOG.log(Level.WARNING, "No greeting found for id {0}", greetingId);
            return;
        }
        
        if (greeting.isDelivered()) {
            LOG.log(Level.WARNING, "Greeting {0} already delivered, skipping.", greetingId);
            return;
        }
        
        LOG.log(Level.INFO, "\ud83c\udf89 DELIVERING GREETING TO: {0} in {1}", new Object[]{
            greeting.getRecipientName(), 
            greeting.getRecipientTimezone()
        });
        
        if (null != greeting.getDeliveryChannel()) {
            LOG.log(Level.INFO, "Pretending to send via channel: {0} to {1}", new Object[]{
                greeting.getDeliveryChannel(), 
                greeting.getContactInfo()
            });
        }
        
        greeting.setDelivered(true);
        greeting.setDeliveredAt(Instant.now());
        
        wsNotifier.broadcastGreetingDelivered(greeting);
    }
}
