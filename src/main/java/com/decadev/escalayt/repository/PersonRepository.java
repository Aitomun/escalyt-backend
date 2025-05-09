package com.decadev.escalayt.repository;


import com.decadev.escalayt.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByEmail(String email);
    Optional<Person> findByUsername(String username); // implemented this
    Optional<Person> findByEmailOrUsername(String email, String username);

    Person findUserByEmail(String email);
    List<Person> findByOrgId(Long orgId);

    @Query("SELECT p FROM Person p JOIN p.tickets t WHERE t.id = :ticketId")
    Optional<Person> findPersonByAssigneeId(Long ticketId);
}
