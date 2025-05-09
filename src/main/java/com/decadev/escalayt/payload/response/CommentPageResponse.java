package com.decadev.escalayt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentPageResponse {
    private List<CommentResponse> comments;
    private boolean hasMore;
}
