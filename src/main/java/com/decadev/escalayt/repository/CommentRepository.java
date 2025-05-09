package com.decadev.escalayt.repository;

import com.decadev.escalayt.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByTicketId(Long ticketId, Pageable pageable);
}
