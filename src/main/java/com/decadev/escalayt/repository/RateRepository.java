package com.decadev.escalayt.repository;

import com.decadev.escalayt.entity.Rate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface RateRepository extends JpaRepository<Rate, Long> {

    List<Rate> findByTicketId(Long ticketId);
    List<Rate> findByPersonId(Long personId);

    boolean existsByTicketIdAndPersonId(Long ticketId, Long id);
    Optional<Rate> findByTicketIdAndPersonId(Long ticketId, Long personId);

}
