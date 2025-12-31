package com.newyear.boundary;

import com.newyear.entity.ScheduledGreeting;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/globe")
@ApplicationScoped
public class GlobeWebSocket {
    
    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void onOpen(final Session session) {
        sessions.add(session);
    }
    
    @OnClose
    public void onClose(final Session session) {
        sessions.remove(session);
    }
    
    public void broadcastGreetingDelivered(final ScheduledGreeting greeting) {
        if (null == greeting.getDeliveredAt()) {
            return;
        }
        
        final JsonObject event = Json.createObjectBuilder()
                .add("type", "GREETING_DELIVERED")
                .add("timezone", greeting.getRecipientTimezone())
                .add("timestamp", greeting.getDeliveredAt().toString())
                .add("recipientName", greeting.getRecipientName())
                .add("message", greeting.getMessage() == null ? "" : greeting.getMessage())
                .build();
        
        final String payload = event.toString();
        
        for (Session session : sessions) {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(payload);
            }
        }
    }
}
