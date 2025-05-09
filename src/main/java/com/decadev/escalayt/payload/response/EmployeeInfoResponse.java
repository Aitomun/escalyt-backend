package com.decadev.escalayt.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeInfoResponse {
    private String fullName;
    private String position;
    private String email;
    private String department;
    private String phoneNumber;
}
