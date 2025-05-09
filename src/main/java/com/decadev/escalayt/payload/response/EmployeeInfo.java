package com.decadev.escalayt.payload.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeInfo {

    private String firstName;

    private String lastName;

    private String email;

    private Long orgId;

    private Long departmentId;

}