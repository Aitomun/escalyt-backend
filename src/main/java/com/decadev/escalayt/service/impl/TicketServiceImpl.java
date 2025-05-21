package com.decadev.escalayt.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.decadev.escalayt.entity.Notification;
import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.entity.Ticket;
import com.decadev.escalayt.enums.Role;
import com.decadev.escalayt.enums.Status;
import com.decadev.escalayt.exceptions.NotFoundException;
import com.decadev.escalayt.exceptions.NotificationException;
import com.decadev.escalayt.exceptions.UserNotFoundException;
import com.decadev.escalayt.payload.request.FilterTicketsRequest;
import com.decadev.escalayt.payload.request.NotificationRequest;
import com.decadev.escalayt.payload.request.TicketRequest;
import com.decadev.escalayt.payload.response.*;
import com.decadev.escalayt.repository.CategoryRepository;
import com.decadev.escalayt.repository.PersonRepository;
import com.decadev.escalayt.repository.TicketRepository;
import com.decadev.escalayt.service.NotificationService;
import com.decadev.escalayt.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

	private final TicketRepository ticketRepository;
	private final PersonRepository personRepository;
	private final CategoryRepository categoryRepository;
	private final NotificationService notificationService;
	private final Cloudinary cloudinary;
	private static final Logger logger = Logger.getLogger(TicketServiceImpl.class.getName());

	@Override
	public TicketResponse createTicket(TicketRequest ticketRequest, String email){
		Optional<Person> person = personRepository.findByEmail(email);
		if (person.isEmpty()) {
			throw new UserNotFoundException("Person not found");
		}

		String fileUrl = null;
		MultipartFile file = ticketRequest.getAttachment();

		if (file != null && !file.isEmpty()) {
			try {
				Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
				fileUrl = uploadResult.get("url").toString();
			} catch (Exception e) {
				throw new RuntimeException("Error uploading file to Cloudinary", e);
			}
		}

		Ticket ticket = Ticket.builder()
				.title(ticketRequest.getTitle())
				.description(ticketRequest.getDescription())
				.priority(ticketRequest.getPriority())
				.status(Status.OPEN)
				.location(ticketRequest.getLocation())
				.attachment(fileUrl)
				.createdBy(person.get().getId())
				.orgId(person.get().getOrgId())
				.assignee(null)
				.createdDate(LocalDateTime.now())
				.category(categoryRepository.findById(ticketRequest.getCategoryId()).orElseThrow(() -> {
					return new NotFoundException("Category not found");
				}))
				.build();

		Ticket savedTicket = ticketRepository.save(ticket);

        NotificationRequest notificationRequest = NotificationRequest.builder()
				.topic("NOTIFICATION")
				.title("NOTIFICATION")
				.body("Ticket created successfully")
				.build();
		try {
			notificationService.sendNotificationToUser(person.get().getId(), notificationRequest);
		}catch (ExecutionException | InterruptedException e) {
			throw new NotificationException("Failed to send notification", e);
		}

		TicketResponse ticketResponse = TicketResponse.builder()
				.createdBy(savedTicket.getCreatedBy())
				.title(savedTicket.getTitle())
				.priority(savedTicket.getPriority())
				.status(savedTicket.getStatus())
				.location(savedTicket.getLocation())
				.description(savedTicket.getDescription())
				.attachment(savedTicket.getAttachment())
				.categoryId(savedTicket.getCategory().getId())
				.build();

		return ticketResponse;
	}

	@Override
	public void deleteTicket(Long ticketId){
		if (!ticketRepository.existsById(ticketId)) {
			throw new NotFoundException("Ticket not found");
		}
		Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new NotFoundException("Ticket not found"));
		Person person = ticket.getAssignee();
		NotificationRequest notificationRequest = NotificationRequest.builder()
				.topic("NOTIFICATION")
				.title("NOTIFICATION")
				.body("Ticket deleted successfully")
				.build();
		try {
			notificationService.sendNotificationToUser(person.getId(), notificationRequest);
		}catch (ExecutionException | InterruptedException e) {
			throw new NotificationException("Failed to send notification", e);
		}		ticketRepository.deleteById(ticketId);
	}

	@Override
	public TicketCountResponse countTickets(String username) {
		Optional<Person> person = personRepository.findByEmail(username);
		if (person.isEmpty()) {
			throw new UserNotFoundException("Person not found");
		}

		if (person.get().getRole() == Role.ADMIN) {
			// If the person is an admin, count tickets by orgId and status
			Long openCount = ticketRepository.countByOrgIdAndStatus(person.get().getOrgId(), Status.OPEN);
			Long inReviewCount = ticketRepository.countByOrgIdAndStatus(person.get().getOrgId(), Status.IN_PROGRESS);
			Long resolvedCount = ticketRepository.countByOrgIdAndStatus(person.get().getOrgId(), Status.RESOLVED);

			return TicketCountResponse.builder()
					.open(openCount)
					.inReview(inReviewCount)
					.resolved(resolvedCount)
					.build();
		} else {
			// If the person is a regular user, count tickets by createdBy and status
			Long openCount = ticketRepository.countByCreatedByAndStatus(person.get().getId(), Status.OPEN);
			Long inReviewCount = ticketRepository.countByCreatedByAndStatus(person.get().getId(), Status.IN_PROGRESS);
			Long resolvedCount = ticketRepository.countByCreatedByAndStatus(person.get().getId(), Status.RESOLVED);

			return TicketCountResponse.builder()
					.open(openCount)
					.inReview(inReviewCount)
					.resolved(resolvedCount)
					.build();
		}

	}

	@Override
	public List<Ticket> getLastThreeOpenTickets() {
		Pageable pageable = PageRequest.of(0, 3); // Page 0 with 3 items per page
		return ticketRepository.findTop3ByStatusOpen(pageable);
	}

	@Override
	public List<Ticket> getLastThreeInProgressTickets() {
		Pageable pageable = PageRequest.of(0, 3);
		return ticketRepository.findTop3ByStatusInProgress(pageable);
	}

	@Override
	public List<Ticket> getLastThreeResolvedTickets() {
		Pageable pageable = PageRequest.of(0, 3);
		return ticketRepository.findTop3ByStatusResolved(pageable);
	}

	/*@Override
	public Page<Ticket> getAllRecentTickets(Long orgId,Pageable pageable) {
		return ticketRepository.findByOrgIdOrderByCreatedDateDesc(orgId, pageable);
	}*/

	@Override
	public Page<TicketDTO> getAllRecentTickets(Long orgId, Pageable pageable) {
		Page<Ticket> ticketPage = ticketRepository.findByOrgIdOrderByCreatedDateDesc(orgId, pageable);
		return ticketPage.map(ticket -> new TicketDTO(
				ticket.getId(),
				ticket.getCreatedBy(),
				ticket.getTitle(),
				ticket.getPriority(),
				ticket.getStatus(),
				ticket.getLocation(),
				ticket.getDescription(),
				ticket.getAttachment(),
				ticket.getOrgId(),
				ticket.getAssignee() != null ? ticket.getAssignee().getFirstName() + " " + ticket.getAssignee().getLastName() : null,
				ticket.getCategory() != null ? ticket.getCategory().getCategoryName() : null,
				null, // comments conversion elsewhere
				null, // handle rates conversion elsewhere
				ticket.getCreatedDate(),
				ticket.isResolved()
		));
	}

	@Override
	public Page<TicketDTO> getUserTickets(String email, Pageable pageable) {
		Person user = personRepository.findUserByEmail(email);

		Page<Ticket> ticketPage = ticketRepository.findByCreatedByOrderByIdDesc(user.getId(), pageable);

		return ticketPage.map(ticket -> new TicketDTO(
				ticket.getId(),
				ticket.getCreatedBy(),
				ticket.getTitle(),
				ticket.getPriority(),
				ticket.getStatus(),
				ticket.getLocation(),
				ticket.getDescription(),
				ticket.getAttachment(),
				ticket.getOrgId(),
				ticket.getAssignee() != null ? ticket.getAssignee().getFirstName() + " " + ticket.getAssignee().getLastName() : null,
				ticket.getCategory() != null ? ticket.getCategory().getCategoryName() : null,
				null, // comments conversion elsewhere
				null, // handle rates conversion elsewhere
				ticket.getCreatedDate(),
				ticket.isResolved()
		));
	}


	@Override
	public List<TicketDTO> filterTickets(FilterTicketsRequest request, Person user) {
		List<Ticket> tickets;

		if (user.getRole() == Role.ADMIN) {
			tickets = ticketRepository.findAll().stream()
					.filter(ticket -> user.getOrgId().equals(ticket.getOrgId()))
					.collect(Collectors.toList());
		} else {
			tickets = ticketRepository.findAll().stream()
					.filter(ticket -> user.equals(ticket.getAssignee()))
					.collect(Collectors.toList());
		}

		if (request.getAssigneeId() != null) {
			tickets = tickets.stream()
					.filter(ticket -> ticket.getAssignee() != null && ticket.getAssignee().getId().equals(request.getAssigneeId()))
					.collect(Collectors.toList());
		}

		if (request.getPriority() != null && !request.getPriority().isEmpty()) {
			tickets = tickets.stream()
					.filter(ticket -> ticket.getPriority() != null &&
							request.getPriority().trim().equalsIgnoreCase(ticket.getPriority().toString().trim()))
					.collect(Collectors.toList());
		}

		if (request.getStatus() != null && !request.getStatus().isEmpty()) {
			tickets = tickets.stream()
					.filter(ticket -> ticket.getStatus() != null && request.getStatus().trim().equalsIgnoreCase(ticket.getStatus().toString().trim()))
					.collect(Collectors.toList());
		}

		if (request.getCategory() != null && !request.getCategory().isEmpty()) {
			tickets = tickets.stream()
					.filter(ticket -> ticket.getCategory() != null && ticket.getCategory().getId().toString().equals(request.getCategory()))
					.collect(Collectors.toList());
		}

		// Log the filtered tickets
		tickets.forEach(ticket -> System.out.println("Filtered Ticket: " + ticket));

		return tickets.stream()
				.map(ticket -> TicketDTO.builder()
						.id(ticket.getId())
						.createdBy(ticket.getCreatedBy())
						.title(ticket.getTitle())
						.priority(ticket.getPriority())
						.status(ticket.getStatus())
						.location(ticket.getLocation())
						.description(ticket.getDescription())
						.attachment(ticket.getAttachment())
						.orgId(ticket.getOrgId())
						.categoryId(ticket.getCategory() != null ? ticket.getCategory().getId().toString() : null)
						.assigneeId(ticket.getAssignee() != null ? ticket.getAssignee().getId().toString() : null)
						.comments(null)
						.rates(null)
						.createdDate(ticket.getCreatedDate())
						.resolved(ticket.isResolved())
						.build())
				.collect(Collectors.toList());
	}


	@Override
	public TicketResponse viewTicketDetails(Long id, String email) {
		Optional<Person> personOpt = personRepository.findByEmail(email);
		if (personOpt.isEmpty()) {
			throw new UserNotFoundException("Person not found");
		}

		Person currentUser = personOpt.get();
		Long orgId = currentUser.getOrgId();

		logger.info("here to get");
		Ticket ticket = ticketRepository.findByIdAndOrgId(id, orgId)
				.orElseThrow(() -> new NotFoundException("Ticket not found for the given organization"));
		logger.info("verify user about ticket: " + ticket);
		// Admins can view all tickets, users can only view tickets they created or are assigned to
		if (!currentUser.getRole().equals(Role.ADMIN) && !ticket.getCreatedBy().equals(currentUser.getId())) {
			throw new SecurityException("Access denied: You are not allowed to view this ticket");
		}
		logger.info("view details");

		return TicketResponse.builder()
				.createdBy(ticket.getCreatedBy())
				.title(ticket.getTitle())
				.priority(ticket.getPriority())
				.status(ticket.getStatus())
				.location(ticket.getLocation())
				.description(ticket.getDescription())
				.attachment(ticket.getAttachment())
				.categoryId(ticket.getCategory().getId())
				.build();
	}

	@Override
	public String markTicketAsResolved(Long id, String email){
		Optional<Person> personOpt = personRepository.findByEmail(email);
		if (personOpt.isEmpty()) {
			throw new UserNotFoundException("Person not found");
		}

		Person currentUser = personOpt.get();
//		Long orgId = currentUser.getOrgId();

		Ticket ticket = ticketRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Ticket not found for the given organization"));

		if (!currentUser.getRole().equals(Role.ADMIN) &&
				!ticket.getCreatedBy().equals(currentUser.getId()) &&
				(ticket.getAssignee() == null || !ticket.getAssignee().getId().equals(currentUser.getId()))) {
			throw new SecurityException("Access denied: You are not allowed to view this ticket");
		}


//		Person person = ticket.getAssignee();
//		NotificationRequest notificationRequest = NotificationRequest.builder()
//				.topic("NOTIFICATION")
//				.title("NOTIFICATION")
//				.body(ticket.getTitle()+ " has been resolved")
//				.build();
//		try {
//			notificationService.sendNotificationToUser(currentUser.getId(), notificationRequest);
//			notificationService.sendNotificationToUser(person.getId(), notificationRequest);
//		}catch (ExecutionException | InterruptedException e) {
//			throw new NotificationException("Failed to send notification", e);
//		}
		ticket.setStatus(Status.RESOLVED);
		ticketRepository.save(ticket);


		return "Ticket has been marked as resolved";
	}

	@Override
	public List<Ticket> getAllTicketsByOrgId(Long orgId) {

		return ticketRepository.findAllByOrgId(orgId);
	}

	@Override
	public List<AllTicketsResponse> getAllUserTickets(Long userId) {
		// Fetch tickets from the repository
		List<Ticket> tickets = ticketRepository.findByCreatedBy(userId);

		// Map tickets to AllTicketsResponse
		List<AllTicketsResponse> allTicketsResponses = tickets.stream()
				.map(ticket -> new AllTicketsResponse(
						ticket.getId(),                                      // Ticket ID
						ticket.getTitle(),                                   // Ticket title
						ticket.getStatus(),                                  // Ticket status
						ticket.getPriority(),                                // Ticket priority
						ticket.getCategory() != null ? ticket.getCategory().getCategoryName() : "Unknown",  // Category name, handle null
						ticket.getAssignee() != null ?
								ticket.getAssignee().getFirstName() + " " + ticket.getAssignee().getLastName() : "Unassigned",  // Assignee name, handle null
						ticket.getCreatedDate(),                             // Ticket creation date
						ticket.getLocation(),                                // Ticket location
						ticket.getCreatedBy() // ✅ Add this

				))
				.collect(Collectors.toList());  // Collect results into a list

		return allTicketsResponses;
	}

	@Override
	public TicketAssignmentResponse assignTicketToUser(Long ticketId, Long userId) {
		Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new NotFoundException("Ticket Not Found"));

		Person user = personRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not Found"));

		ticket.setAssignee(user);
		ticket.setStatus(Status.IN_PROGRESS);

		ticketRepository.save(ticket);

		NotificationRequest notificationRequest = NotificationRequest.builder()
				.topic("NOTIFICATION")
				.title("NOTIFICATION")
				.body( ticket.getTitle() +  " has been assigned to "+ user.getFirstName() +" "+ user.getLastName())
				.build();
		try {
			notificationService.sendNotificationToUser(user.getId(), notificationRequest);
		}catch (ExecutionException | InterruptedException e) {
			throw new NotificationException("Failed to send notification", e);
		}


		String message = "Ticket " + ticket.getTitle() + " has been assigned to user " + user.getFirstName();

		return new TicketAssignmentResponse(ticket,user,message);
	}

	public long countAll() {
		return ticketRepository.count();
	}

	public Map<String, Long> countByStatus() {
        System.out.println("Fetched Ticket Status Distribution:");
        ticketRepository.findAll().forEach(t -> System.out.println(t.getId() + " - " + t.getStatus()));

        return ticketRepository.findAll().stream()
				.collect(Collectors.groupingBy(t -> t.getStatus().toString(), Collectors.counting()));


	}

	public double averageResolutionTime() {
		List<Ticket> resolved = ticketRepository.findByStatus(Status.RESOLVED);
		return resolved.stream()
				.filter(t -> t.getResolvedAt() != null && t.getCreatedDate() != null)
				.mapToLong(t -> Duration.between(t.getCreatedDate(), t.getResolvedAt()).toMinutes())
				.average().orElse(0);
	}

}
