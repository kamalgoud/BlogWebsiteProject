package com.example.blogwebsite.services;

import com.example.blogwebsite.entities.Comment;
import com.example.blogwebsite.entities.Post;
import com.example.blogwebsite.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    public Comment getCommentById(int id) {
        return commentRepository.findById(id).get();
    }

    public void deleteCommentById(int commentId) {
        commentRepository.deleteByIdCustom(commentId);
    }
}
