package com.decadev.escalayt.payload.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    private String categoryName;

    @NotBlank(message = "Description is required")
    private String description;

    // ERR
}
