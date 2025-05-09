package com.decadev.escalayt.payload.request;

import com.decadev.escalayt.entity.Category;
import com.decadev.escalayt.enums.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.*;

import javax.annotation.Nullable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonRegisterRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    private String phoneNumber;


    @NotBlank(message = "Role is required")
    private String position;

    @NotBlank(message = "Username is required")
    private String username;

    private String category;

    private Role role;

    private String profilePicture;

    private String Department;

}
