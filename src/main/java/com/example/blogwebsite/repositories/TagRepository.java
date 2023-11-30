package com.example.blogwebsite.repositories;

import com.example.blogwebsite.entities.Comment;
import com.example.blogwebsite.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    @Query("SELECT p.tags FROM Post p WHERE p.id = :postId")
    Set<Tag> findTagsByPostId(@Param("postId") int postId);

    Tag findByName(String name);
}
