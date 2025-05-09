package com.decadev.escalayt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NotificationToken extends BaseClass{
    @Column(unique = true)
    private String token;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "person_id")
    private Person person;
}