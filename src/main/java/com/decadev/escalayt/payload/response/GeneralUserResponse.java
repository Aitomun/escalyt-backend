package com.decadev.escalayt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GeneralUserResponse<S> {

    private String responseCode;
    private String responseMessage;
    private S data;

    // Constructor with message and data
    public GeneralUserResponse(String responseMessage, S data) {
        this.responseMessage = responseMessage;
        this.data = data;
    }

    // Constructor with only message
    public GeneralUserResponse(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
