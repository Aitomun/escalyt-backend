package com.decadev.escalayt.service.impl;

import com.decadev.escalayt.entity.Comment;
import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.entity.Ticket;
import com.decadev.escalayt.exceptions.NotificationException;
import com.decadev.escalayt.exceptions.UserNotFoundException;
import com.decadev.escalayt.payload.request.CommentRequest;
import com.decadev.escalayt.payload.request.NotificationRequest;
import com.decadev.escalayt.payload.response.CommentPageResponse;
import com.decadev.escalayt.payload.response.CommentResponse;
import com.decadev.escalayt.repository.CommentRepository;
import com.decadev.escalayt.repository.PersonRepository;
import com.decadev.escalayt.repository.TicketRepository;
import com.decadev.escalayt.service.CommentService;
import com.decadev.escalayt.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final PersonRepository personRepository;
    private final NotificationService notificationService;


    @Override
    public CommentResponse addComment(Long ticketId, String email, CommentRequest commentRequest){
        Person person = personRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " + email));
        // Find the ticket by ID and check if it is resolved
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() ->
                new RuntimeException("Ticket not found"));

        if (ticket.isResolved()) {
            throw new RuntimeException("Ticket is already resolved");
        }

        Comment comment = new Comment();
        comment.setMessage(commentRequest.getMessage());
        comment.setPerson(person);
        comment.setTicket(ticket);
        Comment savedComment = commentRepository.save(comment);

        // Check if the ticket has an assignee before sending a notification
        Person assignee = ticket.getAssignee();
        if (assignee != null) {
            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .topic("Notification from " + assignee.getFirstName())
                    .title("Notification from " + assignee.getLastName())
                    .body(commentRequest.getMessage())
                    .build();
            try {
                notificationService.sendNotificationToUser(assignee.getId(), notificationRequest);
            } catch (ExecutionException | InterruptedException e) {
                throw new NotificationException("Failed to send notification", e);
            }
        } else {
            // Handle case where assignee is null (optional)
            System.out.println("No assignee found for ticket ID: " + ticketId);
        }

        // Create and return the response
        CommentResponse response = new CommentResponse();
        response.setId(savedComment.getId());
        response.setMessage(savedComment.getMessage());
        response.setCommenterName(person.getFirstName() + " " + person.getLastName());
        response.setSentAt(LocalDateTime.now());
        return response;
    }

    @Override
    public CommentPageResponse getComments(Long ticketId, int page) {
        Pageable pageable = PageRequest.of(page, 10); // 10 comments per page
        Page<Comment> commentPage = commentRepository.findByTicketId(ticketId, pageable);

        List<CommentResponse> commentResponses = commentPage.stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getMessage(),
                        comment.getPerson().getFirstName() + " " + comment.getPerson().getLastName(),
                        comment.getDateCreated()))
                .collect(Collectors.toList());

        return new CommentPageResponse(commentResponses, commentPage.hasNext());
    }


}

