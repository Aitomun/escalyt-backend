package com.decadev.escalayt.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer ratingCount;

    private String reviewMessage;

    private Long orgId;


    @ManyToOne
    @JoinColumn(name = "ticket_id")
    @JsonManagedReference
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "person_id")
    @JsonManagedReference
    private Person person;
}