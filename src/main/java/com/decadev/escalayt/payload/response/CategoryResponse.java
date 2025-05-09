package com.decadev.escalayt.payload.response;


import com.decadev.escalayt.payload.request.CategoryRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {


    private String responseCode;

    private String responseMessage;




}
