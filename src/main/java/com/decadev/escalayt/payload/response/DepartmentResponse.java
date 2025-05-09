package com.decadev.escalayt.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DepartmentResponse {
    private String departmentName;
    private Long orgId;
}
