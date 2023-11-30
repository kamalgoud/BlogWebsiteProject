package com.example.blogwebsite.repositories;

import com.example.blogwebsite.entities.Comment;
import com.example.blogwebsite.entities.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN p.tags t " +
            "WHERE (" +
            "   (:authorNames IS NULL OR p.author IN :authorNames) " +
            "   AND (:tagIds IS NULL OR t.id IN :tagIds) " +
            ") " +
            "AND ( LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) )")
    Page<Post> filterPosts(
            @Param("authorNames") Set<String> authorNames,
            @Param("tagIds") Set<Integer> tagIds,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    @Query("SELECT DISTINCT p.author FROM Post p")
    Set<String> findAllAuthors();

    @Query("SELECT p.comments FROM Post p where p.id=(:id)")
    List<Comment> findCommentsById(int id);

    @Transactional
    void deleteById(int id);

}
