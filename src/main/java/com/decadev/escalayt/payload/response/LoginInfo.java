package com.decadev.escalayt.payload.response;

import com.decadev.escalayt.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginInfo {
    private String email;
    private String token;
    private Role role;
}