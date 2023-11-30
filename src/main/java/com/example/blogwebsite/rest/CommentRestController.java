package com.example.blogwebsite.rest;

import com.example.blogwebsite.entities.Comment;
import com.example.blogwebsite.entities.Post;
import com.example.blogwebsite.entities.User;
import com.example.blogwebsite.repositories.UserRepository;
import com.example.blogwebsite.services.CommentService;
import com.example.blogwebsite.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/api")
public class CommentRestController {

    private final PostService postService;
    private final CommentService commentService;

    private final UserRepository userRepository;

    @Autowired
    public CommentRestController(PostService postService, CommentService commentService, UserRepository userRepository) {
        this.postService = postService;
        this.commentService = commentService;
        this.userRepository = userRepository;

    }

    @PostMapping("/savecomment")
    public Comment saveComment(@RequestParam("id") int postId,
                               @RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("comment") String content) {
        Comment comment = new Comment();
        comment.setName(name);
        comment.setEmail(email);
        comment.setComment(content);
        Post post = postService.getPostById(postId);
        post.getComments().add(comment);
        postService.savePost(post);
        commentService.save(comment);
        return comment;
    }

    @DeleteMapping("/deletecomment")
    public String deleteComment(@RequestParam("id") int postId,
                                @RequestParam("commentid") int commentId) {
        Comment comment = commentService.getCommentById(commentId);
        Post post = postService.getPostById(postId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        if (user.getAuthority().equals("author")) {
            if (!user.getPosts().contains(post)) {
                return "access-denied";
            }
        }

        post.getComments().remove(comment);
        postService.savePost(post);
        commentService.deleteCommentById(commentId);
        return "comment deleted";
    }

    @PutMapping("/commentupdationcompletion")
    public String updatedComment(@RequestParam("id") int postId,
                                 @RequestParam("commentid") int commentId,
                                 @RequestParam("name") String name,
                                 @RequestParam("email") String email,
                                 @RequestParam("comment") String comment) {

        Post post = postService.getPostById(postId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        if (user.getAuthority().equals("author")) {
            if (!user.getPosts().contains(post)) {
                return "access-denied";
            }
        }

        Comment commentObj = commentService.getCommentById(commentId);
        commentObj.setName(name);
        commentObj.setEmail(email);
        commentObj.setComment(comment);
        commentService.save(commentObj);
        return "Comment Updated";
    }

}
