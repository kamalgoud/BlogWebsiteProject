package com.example.blogwebsite.services;

import com.example.blogwebsite.entities.Comment;
import com.example.blogwebsite.entities.Post;
import com.example.blogwebsite.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Page<Post> getPostsWithPaginationAndSorting(int pageNo, int pageSize, Sort sort) {//offset=pageNumber
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<Post> posts = postRepository.findAll(pageRequest);
        return posts;
    }

    public List<Comment> getCommentsByPostId(int postId) {
        return postRepository.findCommentsById(postId);
    }

    public Post getPostById(int postId) {
        return postRepository.findById(postId).get();
    }


    public void savePost(Post post) {
        postRepository.save(post);
    }

    public void deletePost(int postId) {
        postRepository.deleteById(postId);
    }

    public void updatePost(Post updatedPost) {
        Optional<Post> existingPostOptional = postRepository.findById(updatedPost.getId());

        if (existingPostOptional.isPresent()) {
            Post existingPost = existingPostOptional.get();
            existingPost.setTitle(updatedPost.getTitle());
            existingPost.setAuthor(updatedPost.getAuthor());
            existingPost.setUpdatedAt(updatedPost.getUpdatedAt());
            existingPost.setContent(updatedPost.getContent());
            existingPost.setTags(updatedPost.getTags());
            postRepository.save(existingPost);
        } else {
            postRepository.save(updatedPost);
        }
    }
}
