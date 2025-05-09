package com.decadev.escalayt.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

@EnableJpaAuditing
@MappedSuperclass
@Data
public class BaseClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime dateCreated;

    @LastModifiedDate
    private LocalDateTime dateModified;

    @PrePersist
    @PreUpdate
    private void prePersist(){
        if(dateCreated == null){
            dateCreated = LocalDateTime.now();
        }
        dateModified = LocalDateTime.now();
    }
}