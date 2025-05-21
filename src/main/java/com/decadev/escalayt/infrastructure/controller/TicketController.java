package com.decadev.escalayt.infrastructure.controller;

import com.decadev.escalayt.entity.Comment;
import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.entity.Ticket;
import com.decadev.escalayt.enums.Priority;
import com.decadev.escalayt.exceptions.NotFoundException;
import com.decadev.escalayt.exceptions.PersonNotFoundException;
import com.decadev.escalayt.exceptions.UserNotFoundException;
import com.decadev.escalayt.payload.request.CategoryRequest;
import com.decadev.escalayt.payload.request.CommentRequest;
import com.decadev.escalayt.payload.request.FilterTicketsRequest;
import com.decadev.escalayt.payload.request.TicketRequest;
import com.decadev.escalayt.payload.response.*;
import com.decadev.escalayt.repository.PersonRepository;
import com.decadev.escalayt.service.CategoryService;
import com.decadev.escalayt.service.CommentService;
import com.decadev.escalayt.service.PersonService;
import com.decadev.escalayt.service.TicketService;
import com.sun.security.auth.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

	private final TicketService ticketService;
	private final PersonRepository userRepository;
	private final CommentService commentService;
	private final PersonService personService;

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	@PostMapping("/create/{id}")
	public ResponseEntity<?> createTicket(
			@PathVariable Long id,
			@RequestParam("title") String title,
			@RequestParam("location") String location,
			@RequestParam("priority") Priority priority,
			@RequestParam("description") String description,
			@RequestPart(value = "file", required = false) MultipartFile file
	) throws ExecutionException, InterruptedException {

		// Get the authenticated user
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUsername = authentication.getName();

		// Prepare the request DTO
		TicketRequest ticketRequestDto = new TicketRequest();
		ticketRequestDto.setTitle(title);
		ticketRequestDto.setLocation(location);
		ticketRequestDto.setPriority(priority);
		ticketRequestDto.setDescription(description);
		ticketRequestDto.setAttachment(file); // Optional file
		ticketRequestDto.setCategoryId(id);

		// Call the service to create the ticket
		TicketResponse response = ticketService.createTicket(ticketRequestDto, currentUsername);

		return ResponseEntity.ok(response);
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteTicket(@PathVariable Long id) throws ExecutionException, InterruptedException {
		ticketService.deleteTicket(id);
		return new ResponseEntity<>("Ticket deleted successfully", HttpStatus.OK);
	}


	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	@GetMapping("/count")
	public ResponseEntity<TicketCountResponse> countTickets() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUser = authentication.getName();

		TicketCountResponse response = ticketService.countTickets(currentUser);

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	@GetMapping("/last-three-open-tickets")
	public ResponseEntity<List<Ticket>> getLastThreeOpenTickets() {
		List<Ticket> tickets = ticketService.getLastThreeOpenTickets();
		return ResponseEntity.ok(tickets);
	}


	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	@GetMapping("/last-three-in-progress-tickets")
	public ResponseEntity<List<Ticket>> getLastThreeInProgressTickets() {
		List<Ticket> tickets = ticketService.getLastThreeInProgressTickets();
		return ResponseEntity.ok(tickets);
	}


	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	@GetMapping("/last-three-resolved-tickets")

	public ResponseEntity<List<TicketResponse>> getLastThreeResolvedTickets() {
		List<Ticket> tickets = ticketService.getLastThreeResolvedTickets();
		List<TicketResponse> dtos = tickets.stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}

	private TicketResponse toResponse(Ticket t) {
		return TicketResponse.builder()
				.createdBy(t.getCreatedBy())
				.title(t.getTitle())
				.priority(t.getPriority())
				.status(t.getStatus())
				.location(t.getLocation())
				.description(t.getDescription())
				.attachment(t.getAttachment())
				.categoryId(t.getCategory().getId())
				// etc. – only the fields you actually need
				.build();
	}



	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin/recent")
	public Page<TicketDTO> getRecentTickets(@RequestParam(defaultValue = "0") int page,
											@RequestParam(defaultValue = "7") int size) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		Person person = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

		Long orgId = person.getOrgId();
		Pageable pageable = PageRequest.of(page, size);

		return ticketService.getAllRecentTickets(orgId, pageable);
	}


	@PreAuthorize("hasRole('USER')")
	@GetMapping("/user/recent")
	public Page<TicketDTO> getUserRecentTickets(@RequestParam(defaultValue = "0") int page,
												@RequestParam(defaultValue = "7") int size) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Pageable pageable = PageRequest.of(page, size);
		String email = authentication.getName();
		return ticketService.getUserTickets(email, pageable);
	}


//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	@PostMapping("/filter")
	public ResponseEntity<FilterTicketsResponse> filterTickets(@RequestBody FilterTicketsRequest request, Authentication authentication) {
		Person user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
		List<TicketDTO> tickets = ticketService.filterTickets(request, user);
		String message = tickets.isEmpty() ? "No tickets found" : "Tickets retrieved successfully";

		// Log the filtering request and response
		System.out.println("Filtering Request: " + request);
		System.out.println("Filtered Tickets: " + tickets);

		return ResponseEntity.ok(new FilterTicketsResponse(message, tickets));
	}





	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	@GetMapping("/details/{id}")
	public ResponseEntity<TicketResponse> viewTicketDetails(
			@PathVariable Long id,
			@AuthenticationPrincipal UserDetails userDetails) {

		String email = userDetails.getUsername(); // This retrieves the username (email) from UserDetails

		try {
			TicketResponse response = ticketService.viewTicketDetails(id, email);
			return ResponseEntity.ok(response);
		} catch (UserNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (NotFoundException ex) {
			return ResponseEntity.notFound().build();
		} catch (SecurityException ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	@PutMapping("/resolve/{id}")
	public ResponseEntity<String> resolveTicket(
			@PathVariable Long id,
			@RequestBody TicketResponse ticketResponse,
			@AuthenticationPrincipal UserDetails userDetails) {

		String email = userDetails.getUsername(); // This retrieves the username (email) from UserDetails

		try {
			String responseMessage = ticketService.markTicketAsResolved(id, email);
			return ResponseEntity.ok(responseMessage);
		} catch (UserNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
		} catch (NotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		} catch (SecurityException ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		} catch (Exception ex) {
			// Log the exception details for debugging
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while resolving the ticket");
		}
	}

	@PostMapping("/comment/{ticketId}")
	public ResponseEntity<?> addComment(@PathVariable Long ticketId, @RequestBody @Valid CommentRequest commentRequest) {
		try {
			// Get the email of the authenticated user
			String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

			// Call the service to add the comment
			CommentResponse commentResponse = commentService.addComment(ticketId, email, commentRequest);

			// Return the response with the added comment
			return ResponseEntity.ok(commentResponse);
		} catch (UserNotFoundException e) {
			// Handle case where the user is not found
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		} catch (NotFoundException e) {
			// Handle case where the ticket is not found
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket not found");
		} catch (RuntimeException e) {
			// Log the exception details and return a bad request response
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Invalid input or other error occurred");
		} catch (Exception e) {
			// Handle any other unexpected exceptions
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
		}
	}

	//To Get a list of all the tickets in the organization
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin/all")
	public List<Ticket> getAllTicketsByOrgId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		Person person = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
		if (person == null) {
			throw new RuntimeException("User not found");
		}
		Long orgId = person.getOrgId();

		return ticketService.getAllTicketsByOrgId(orgId);
	}

	//To Get a list of all the tickets the user created
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	@GetMapping("/list-user")
	public ResponseEntity<?> getTicketsCreatedByUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

		Long userId = userRepository.findUserByEmail(email).getId();

		List<AllTicketsResponse> ticketList= ticketService.getAllUserTickets(userId);

		return ResponseEntity.ok(ticketList);
	}
	// To Assign a ticket to a user
	@PutMapping("/assign")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<TicketAssignmentResponse> assignTicketToUser(@RequestParam Long ticketId, @RequestParam Long userId){
		TicketAssignmentResponse response = ticketService.assignTicketToUser(ticketId,userId);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	@GetMapping("/employee/{id}")
	public ResponseEntity<EmployeeInfoResponse> getEmployeeDetails(@PathVariable Long id) {
		try {
			EmployeeInfoResponse response = personService.getEmployeeDetailsById(id);
			return ResponseEntity.ok(response);
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}
	@GetMapping("/{ticketId}/comments")
	public ResponseEntity<CommentPageResponse> getComments(@PathVariable Long ticketId, @RequestParam int page) {
		// Implementation to fetch comments with pagination
		return ResponseEntity.ok(commentService.getComments(ticketId, page));
	}



	@GetMapping("/assign/{ticketId}")
	public ResponseEntity<PersonResponseDTO> getAssigneeByTicketId(@PathVariable Long ticketId) {
		Optional<PersonResponseDTO> personResponse = personService.findPersonByAssigneeId(ticketId);
		return personResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}


}