package com.decadev.escalayt.payload.response;

import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketAssignmentResponse {


    private Long ticketId;

    private String ticketTitle;

    private String ticketPriority;

    private String ticketLocation;

    private String ticketDescription;

    private String assigneeName;

    private  String assigneePhone;

    private String assigneeEmail;

    private String assigneePosition;

    private String status;

    private String message;

    public TicketAssignmentResponse(Ticket ticket, Person user, String message) {
        this.ticketId = ticket.getId();
        this.ticketTitle = ticket.getTitle();
        this.ticketPriority = ticket.getPriority().toString(); // Convert priority to string if needed
        this.ticketLocation = ticket.getLocation();
        this.ticketDescription = ticket.getDescription();
        this.assigneeName = user.getFirstName() + " " + user.getLastName();
        this.assigneeEmail = user.getEmail();
        this.assigneePhone = user.getPhoneNumber();
        this.assigneePosition = user.getPosition();
        this.status = ticket.getStatus().toString();
        this.message = message;
    }



}
