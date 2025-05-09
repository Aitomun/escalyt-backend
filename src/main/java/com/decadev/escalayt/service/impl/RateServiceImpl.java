package com.decadev.escalayt.service.impl;
import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.entity.Rate;
import com.decadev.escalayt.entity.Ticket;
import com.decadev.escalayt.exceptions.EntityNotFoundException;
import com.decadev.escalayt.exceptions.InvalidRatingException;
import com.decadev.escalayt.exceptions.TicketNotResolvedException;
import com.decadev.escalayt.payload.request.RateRequest;
import com.decadev.escalayt.payload.response.RateResponse;
import com.decadev.escalayt.repository.PersonRepository;
import com.decadev.escalayt.repository.RateRepository;
import com.decadev.escalayt.repository.TicketRepository;
import com.decadev.escalayt.service.RateService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateServiceImpl implements RateService {
    private final RateRepository rateRepository;
    private final TicketRepository ticketRepository;
    private final PersonRepository personRepository; // Ensure you have this repository

    @Override
    public RateResponse createRating(Long ticketId, RateRequest rateRequest) throws MessagingException {
        // Retrieve the ticket
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        // Extract the person information from the security context
        String username = getCurrentUsername();
        Person person = personRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("Person not found"));

        // Validate rating
        if (rateRequest.getRatingCount() < 1 || rateRequest.getRatingCount() > 5) {
            throw new InvalidRatingException("Rating must be between 1 and 5");
        }

        // Check if the ticket is resolved
        if (!"RESOLVED".equalsIgnoreCase(String.valueOf(ticket.getStatus()))) {
            throw new TicketNotResolvedException("Cannot rate a ticket that is not resolved");
        }

        // Create a new Rate entity
        Rate rate = new Rate();
        rate.setTicket(ticket);
        rate.setPerson(person);
        rate.setRatingCount(rateRequest.getRatingCount());
        rate.setReviewMessage(rateRequest.getReviewMessage());

        // Save the Rate entity
        Rate savedRate = rateRepository.save(rate);

        // Construct the response
        RateResponse rateResponse = new RateResponse();
        rateResponse.setId(savedRate.getId());
        rateResponse.setTicketId(savedRate.getTicket().getId());
        rateResponse.setPersonId(savedRate.getPerson().getId());
        rateResponse.setRatingCount(savedRate.getRatingCount());
        rateResponse.setReviewMessage(savedRate.getReviewMessage());

        return rateResponse;
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}