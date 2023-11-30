package com.example.blogwebsite.repositories;

import com.example.blogwebsite.entities.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.id = :commentId")
    void deleteByIdCustom(int commentId);
}
