package com.decadev.escalayt.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Comment extends BaseClass {

    private String message;

    @ManyToOne
    @JoinColumn(name = "person_id")
    @JsonManagedReference
    private Person person;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    @JsonManagedReference
    private Ticket ticket;

    private Long orgId;
}