package com.taskforge.repository;

import com.taskforge.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTask_IdOrderByCreatedAtDesc(Long taskId);
}
