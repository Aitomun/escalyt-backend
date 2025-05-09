package com.decadev.escalayt.payload.response;

import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailResponse {

    private String responseCode;
    private String responseMessage;
    private String firstName;
    private String lastName;
    private String department;
    private String position;
    private String username;
    private String email;
    private Role role;
    private String phoneNumber;
    private String profilePicture;
    private String password;
    private String personCategory;
    private Long orgId;
}
