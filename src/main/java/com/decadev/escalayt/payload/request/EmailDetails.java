package com.decadev.escalayt.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetails {


    private String firstName;

    private String lastName;

    private String recipient;

    private String messageBody;

    private String subject;

    private String attachment;

    private String link;
}
