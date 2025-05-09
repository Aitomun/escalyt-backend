package com.decadev.escalayt.infrastructure.controller;


import com.decadev.escalayt.exceptions.EntityNotFoundException;
import com.decadev.escalayt.exceptions.InvalidRatingException;
import com.decadev.escalayt.exceptions.TicketNotResolvedException;
import com.decadev.escalayt.payload.request.RateRequest;
import com.decadev.escalayt.payload.response.RateResponse;
import com.decadev.escalayt.service.RateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RateController {

    private final RateService rateService;

//    @PostMapping
//    public ResponseEntity<RateResponse> rateTicket(@RequestBody @Valid RateRequest rateRequest) throws MessagingException {
//        RateResponse rateResponse = rateService.createRating(rateRequest);
//        return new ResponseEntity<>(rateResponse, HttpStatus.CREATED);
//    }
@PostMapping("/{ticketId}")
public ResponseEntity<?> rateTicket(@PathVariable Long ticketId, @RequestBody @Valid RateRequest rateRequest) {
    try {
        RateResponse rateResponse = rateService.createRating(ticketId, rateRequest);
        return new ResponseEntity<>(rateResponse, HttpStatus.CREATED);
    } catch (EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    } catch (InvalidRatingException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (TicketNotResolvedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
}


