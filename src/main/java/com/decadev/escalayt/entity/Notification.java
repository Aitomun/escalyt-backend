package com.decadev.escalayt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Notification extends BaseClass {
    private String title;
    private String body;
    private String topic;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "person_id")
    private Person person;
}
