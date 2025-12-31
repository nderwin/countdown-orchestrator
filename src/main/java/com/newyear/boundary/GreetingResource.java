package com.newyear.boundary;

import com.newyear.control.GreetingRequest;
import com.newyear.control.GreetingSchedulerService;
import com.newyear.entity.ScheduledGreeting;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/greetings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class GreetingResource {
    
    @Inject
    GreetingSchedulerService scheduler;
    
    @POST
    public Response scheduleGreeting(final GreetingRequest request) {
        if (null == request || null == request.recipientTimezone()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("recipientTimezone is required")
                    .build();
        }
        
        final ScheduledGreeting greeting = scheduler.scheduleGreeting(request);
        // TODO - make a proper 201 response
        // TODO - return a projection instead
        return Response.status(Response.Status.CREATED)
                .entity(greeting)
                .build();
    }
    
    @GET
    public List<ScheduledGreeting> listGreetings() {
        // TODO - return a projection instead
        return ScheduledGreeting.listAll();
    }
}
