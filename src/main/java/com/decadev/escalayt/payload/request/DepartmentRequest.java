package com.decadev.escalayt.payload.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DepartmentRequest {

    private String departmentName;
    private Long orgId;
}
