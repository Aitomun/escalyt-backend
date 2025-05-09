package com.decadev.escalayt.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category name is required")
    private String categoryName;

    @NotBlank(message = "Description is required")
    private String description;

    private Long orgId;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Ticket> tickets;

    @OneToMany(mappedBy =  "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Person> persons;
}
