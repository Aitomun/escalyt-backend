package com.decadev.escalayt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Department extends BaseClass {

    private String departmentName;

    private Long orgId; // Ensure this field exists

    @OneToMany(mappedBy =  "employeeDepartment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Person> persons; // Reference to the person who created this department


}