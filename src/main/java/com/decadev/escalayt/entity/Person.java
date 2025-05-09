
package com.decadev.escalayt.entity;

import com.decadev.escalayt.enums.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name= "user_tbl")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person extends BaseClass implements UserDetails {

    @NotBlank(message = "FirstName is required")
    private String firstName;

    @NotBlank(message = "Last is required")
    private String lastName;


    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rate> rates;
    @Nullable
    private String department;

    @NotBlank(message = "Role is required")
    private String position;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String phoneNumber;

    private String profilePicture;

    @NotBlank(message = "Password is required")
    private String password;

    @Transient
    private String confirmPassword; // this field won't be persisted in the database,  it's used transiently for validation purposes only.

    private String personCategory;

    @Nullable
    private Long orgId;

    private boolean enabled = false;



    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonManagedReference
    private Category category;


    @OneToMany(mappedBy = "person")
    private List<JToken> jtokens;

    @OneToMany(mappedBy = "person")
    private List<NotificationToken> notificationTokens;

    @OneToMany(mappedBy = "person")
    private List<Notification> notifications;


    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonManagedReference
    private Department employeeDepartment;

    public Person(Long personId) {
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}