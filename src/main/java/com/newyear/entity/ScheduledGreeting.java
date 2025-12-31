package com.newyear.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "scheduled_greetings")
public class ScheduledGreeting extends PanacheEntity {
    
    @Column(name = "sendername", nullable = false)
    private String senderName;
    
    @Column(name = "recipientname", nullable = false)
    private String recipientName;
    
    @Column(name = "recipienttimezone", nullable = false)
    private String recipientTimezone;
    
    @Column(name = "message", length = 1000)
    private String message = "";
    
    @Column(name = "targetdeliverytime", nullable = false)
    private ZonedDateTime targetDeliveryTime;
    
    @Column(name = "quartzjobid")
    private String quartzJobId;
    
    @Column(name = "delivered", nullable = false)
    private boolean delivered = false;
    
    @Column(name = "deliveredat")
    private Instant deliveredAt;
    
    @Column(name = "deliverychannel")
    private String deliveryChannel;
    
    @Column(name = "contactinfo")
    private String contactInfo;

    protected ScheduledGreeting() {
    }

    public ScheduledGreeting(final String senderName, final String recipientName, final String recipientTimezone, final ZonedDateTime targetDeliveryTime) {
        if (null == senderName) {
            throw new IllegalArgumentException("sender name must not be null");
        }
        
        if (null == recipientName) {
            throw new IllegalArgumentException("recipient name must not be null");
        }
        
        if (null == recipientTimezone) {
            throw new IllegalArgumentException("recipient timezone must not be null");
        }

        if (null == targetDeliveryTime) {
            throw new IllegalArgumentException("target delivery time must not be null");
        }
        this.senderName = senderName;
        this.recipientName = recipientName;
        this.recipientTimezone = recipientTimezone;
        this.targetDeliveryTime = targetDeliveryTime;
    }

    public ScheduledGreeting(final String senderName, final String recipientName, final String recipientTimezone, final ZonedDateTime targetDeliveryTime, final String message) {
        this(senderName, recipientName, recipientTimezone, targetDeliveryTime);
        this.message = message;
    }
    
    public static List<ScheduledGreeting> findPendingByTimeRange(final ZonedDateTime start, final ZonedDateTime end) {
        return find("delivered = false and targetDeliveryTime between ?1 and ?2", start, end).list();
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(final String senderName) {
        if (null == senderName) {
            throw new IllegalArgumentException("sender name must not be null");
        }

        this.senderName = senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(final String recipientName) {
        if (null == recipientName) {
            throw new IllegalArgumentException("recipient name must not be null");
        }

        this.recipientName = recipientName;
    }

    public String getRecipientTimezone() {
        return recipientTimezone;
    }

    public void setRecipientTimezone(final String recipientTimezone) {
        if (null == recipientTimezone) {
            throw new IllegalArgumentException("recipient timezone must not be null");
        }

        this.recipientTimezone = recipientTimezone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public ZonedDateTime getTargetDeliveryTime() {
        return targetDeliveryTime;
    }

    public void setTargetDeliveryTime(final ZonedDateTime targetDeliveryTime) {
        if (null == targetDeliveryTime) {
            throw new IllegalArgumentException("target delivery time must not be null");
        }

        this.targetDeliveryTime = targetDeliveryTime;
    }

    public String getQuartzJobId() {
        return quartzJobId;
    }

    public void setQuartzJobId(final String quartzJobId) {
        this.quartzJobId = quartzJobId;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(final boolean delivered) {
        this.delivered = delivered;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(final Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getDeliveryChannel() {
        return deliveryChannel;
    }

    public void setDeliveryChannel(final String deliveryChannel) {
        this.deliveryChannel = deliveryChannel;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(final String contactInfo) {
        this.contactInfo = contactInfo;
    }
    
}
