package com.decadev.escalayt.repository;


import com.decadev.escalayt.entity.Ticket;
import com.decadev.escalayt.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Long countByOrgIdAndStatus (Long OrgId, Status status); // count for admin

    Long countByCreatedByAndStatus(Long createdBy, Status status); // count for user
/*
    @Query("SELECT t FROM Ticket t WHERE t.status = 'OPEN' ORDER BY t.createdDate DESC")
    List<Ticket> findTop3ByStatusOpen();*/

    @Query("SELECT t FROM Ticket t WHERE t.status = 'OPEN' ORDER BY t.createdDate DESC")
    List<Ticket> findTop3ByStatusOpen(Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.status = 'IN_PROGRESS' ORDER BY t.createdDate DESC")
    List<Ticket> findTop3ByStatusInProgress(Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.status = 'RESOLVED' ORDER BY t.createdDate DESC")
    List<Ticket> findTop3ByStatusResolved(Pageable pageable);

    Page<Ticket> findByOrgIdOrderByCreatedDateDesc(Long orgId, Pageable pageable);


    Page<Ticket> findByCreatedByOrderByIdDesc(Long createdBy, Pageable pageable);

    Optional<Ticket> findByIdAndOrgId(Long id, Long orgId);

    Optional<Ticket> findById(Long id);

    List<Ticket> findAllByOrgId(Long orgId);

    List<Ticket> findByCreatedBy(Long createdBy);

    List<Ticket> findByStatus(Status status);
}