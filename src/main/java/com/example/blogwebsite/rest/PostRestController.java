package com.example.blogwebsite.rest;

import com.example.blogwebsite.entities.Post;
import com.example.blogwebsite.entities.User;
import com.example.blogwebsite.repositories.PostRepository;
import com.example.blogwebsite.repositories.UserRepository;
import com.example.blogwebsite.services.CommentService;
import com.example.blogwebsite.services.PostService;
import com.example.blogwebsite.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PostRestController {

    private final PostService postService;
    private final CommentService commentService;
    private final TagService tagService;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    @Autowired
    public PostRestController(PostService postService, CommentService commentService,
                              TagService tagService, PostRepository postRepository, UserRepository userRepository) {
        this.postService = postService;
        this.commentService = commentService;
        this.tagService = tagService;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/getallpost")
    public List<Post> viewAllPost() {
        return postRepository.findAll();
    }

    @GetMapping("/getpostbyid/{id}")
    public Post getPostById(@PathVariable int id) {
        return postService.getPostById(id);
    }

    @PostMapping("/addthepost")
    public String addingPost(@RequestBody Post newPost) {
        Post post = postRepository.save(newPost);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());

        if (user.getAuthority().equals("author")) {
            post.setAuthor(user.getName());
        }

        return "Data Successfully Added";
    }

    @DeleteMapping("/deletethepost/{id}")
    public String deletingPost(@PathVariable int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        System.out.println(user.getAuthority());

        Optional<Post> optionalPost = postRepository.findById(id);

        if (user.getAuthority().equals("author")) {
            if (!user.getPosts().contains(optionalPost.get())) {
                return "Access denied";
            }
        }

        postRepository.deleteById(id);
        return "Data Deleted Added";
    }

    @PutMapping("/updatethepost/{id}")
    public String updatingPost(@PathVariable int id, @RequestBody Post updatedPost) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        System.out.println(user.getAuthority());

        Optional<Post> optionalPost = postRepository.findById(id);

        System.out.println("authentication");

        if (user.getAuthority().equals("author")) {
            if (!user.getPosts().contains(optionalPost.get())) {
                return "Access denied";
            }
        }

        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();
            if (updatedPost.getTitle() != null) {
                existingPost.setTitle(updatedPost.getTitle());
            }
            if (updatedPost.getContent() != null) {
                existingPost.setContent(updatedPost.getContent());
            }
            if (updatedPost.getAuthor() != null) {
                existingPost.setAuthor(updatedPost.getAuthor());
            }
            if (updatedPost.getTags() != null) {
                existingPost.setTags(updatedPost.getTags());
            }
            postRepository.save(existingPost);
            return "Data Updated Successfully";
        } else {
            return "Post with ID " + id + " not found";
        }
    }


}
