package com.newyear.control;

public record GreetingRequest(
        String senderName,
        String recipientName,
        String recipientTimezone,
        String message,
        String deliveryChannel,
        String contactInfo,
        boolean testMode
        ) {

}
