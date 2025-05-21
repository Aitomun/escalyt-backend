package com.decadev.escalayt.entity;

import com.decadev.escalayt.enums.Priority;
import com.decadev.escalayt.enums.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Ticket {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long createdBy;

    private String title;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String location;

    private String description;

    private String attachment;

    private Long orgId;


    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    @JsonBackReference("ticket-assignee")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Person assignee;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference("ticket-category")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Category category;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Rate> rates;


    @Column(name = "created_date")
    private LocalDateTime createdDate;

    public boolean isResolved() {
        return
                this.status == Status.RESOLVED;
    }

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }


}