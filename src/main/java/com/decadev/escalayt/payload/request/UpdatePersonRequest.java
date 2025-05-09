package com.decadev.escalayt.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePersonRequest {

    @NotBlank(message = "FirstName must not be blank")
    private String firstName;

    @NotBlank(message = "LastName must not be blank ")
    private String lastName;

    @NotBlank(message = "Department must not be blank")
    private String Department;

    @NotBlank(message = "PhoneNumber must not be blank")
    private String phoneNumber;

    private String profilePicture;

    @NotBlank(message = "Position must not be blank")
    private String position;

    @NotBlank(message = "Username must not be blank")
    private String username;
}
