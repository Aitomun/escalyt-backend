package com.decadev.escalayt.service;

import com.decadev.escalayt.entity.Comment;
import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.payload.request.CommentRequest;
import com.decadev.escalayt.payload.response.CommentPageResponse;
import com.decadev.escalayt.payload.response.CommentResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CommentService {

    CommentResponse addComment(Long ticketId, String email, CommentRequest commentRequest) throws ExecutionException, InterruptedException;


    CommentPageResponse getComments(Long ticketId, int page);


}
