package com.decadev.escalayt.payload.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class NewEmployeeResponse {

    private String responseCode;
    private String responseMessage;
    private EmployeeInfo employeeInfo;

    private Long departmentId;
}
