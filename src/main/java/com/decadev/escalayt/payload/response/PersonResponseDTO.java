package com.decadev.escalayt.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonResponseDTO {
    private String name;
    private String email;
    private String position;
    private String phoneNumber;
}
