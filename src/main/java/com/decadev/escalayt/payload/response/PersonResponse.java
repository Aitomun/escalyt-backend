package com.decadev.escalayt.payload.response;

import com.decadev.escalayt.entity.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonResponse {
    private String responseCode;
    private String responseMessage;
    private List<Person> persons;
}
