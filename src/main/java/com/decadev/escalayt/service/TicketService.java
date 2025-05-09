package com.decadev.escalayt.service;

import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.entity.Ticket;
import com.decadev.escalayt.payload.request.FilterTicketsRequest;
import com.decadev.escalayt.payload.request.TicketRequest;
import com.decadev.escalayt.payload.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface TicketService {

	TicketResponse createTicket(TicketRequest ticketRequest, String email) throws ExecutionException, InterruptedException;


	void deleteTicket(Long ticketId) throws ExecutionException, InterruptedException;

	TicketCountResponse countTickets(String username);

	List<Ticket> getLastThreeOpenTickets();

	List<Ticket> getLastThreeInProgressTickets();

	List<Ticket> getLastThreeResolvedTickets();

	Page<TicketDTO> getAllRecentTickets(Long orgId, Pageable pageable);

	Page<TicketDTO> getUserTickets(String email, Pageable pageable);

	List<TicketDTO> filterTickets(FilterTicketsRequest request, Person user);

	TicketResponse viewTicketDetails(Long id, String email);

	String markTicketAsResolved(Long id, String email) throws ExecutionException, InterruptedException;

	List<Ticket> getAllTicketsByOrgId(Long orgId);

	List<AllTicketsResponse> getAllUserTickets(Long userId);

	TicketAssignmentResponse assignTicketToUser(Long ticketId, Long userId);
}
