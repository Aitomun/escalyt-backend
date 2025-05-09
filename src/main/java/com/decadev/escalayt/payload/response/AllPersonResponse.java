package com.decadev.escalayt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder



    public class AllPersonResponse {

        private String responseCode;

        private String responseMessage;

        private List<Map<String, Object>> allPersons;




    }