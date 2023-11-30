package com.example.blogwebsite.controllers;

import com.example.blogwebsite.entities.Comment;
import com.example.blogwebsite.entities.Post;
import com.example.blogwebsite.entities.User;
import com.example.blogwebsite.repositories.UserRepository;
import com.example.blogwebsite.services.CommentService;
import com.example.blogwebsite.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CommentController {

    private final PostService postService;
    private final CommentService commentService;

    private final UserRepository userRepository;

    @Autowired
    public CommentController(PostService postService, CommentService commentService, UserRepository userRepository) {
        this.postService = postService;
        this.commentService = commentService;
        this.userRepository = userRepository;

    }

    @RequestMapping("/savecomment")
    public String saveComment(@RequestParam("id") int postId,
                              @RequestParam("name") String name,
                              @RequestParam("email") String email,
                              @RequestParam("comment") String content,
                              RedirectAttributes redirectAttributes) {
        Comment comment = new Comment();
        comment.setName(name);
        comment.setEmail(email);
        comment.setComment(content);
        Post post = postService.getPostById(postId);
        post.getComments().add(comment);
        postService.savePost(post);
        commentService.save(comment);
        redirectAttributes.addAttribute("id", postId);
        return "redirect:/viewpost";
    }

    @RequestMapping("/deletecomment")
    public String deleteComment(@RequestParam("id") int postId,
                                @RequestParam("commentid") int commentId,
                                RedirectAttributes redirectAttributes) {
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
        redirectAttributes.addAttribute("id", postId);
        return "redirect:/viewpost";
    }

    @RequestMapping("/updatecomment")
    public String updateCommet(@RequestParam("commentid") int id, @RequestParam("id") int postId, Model model) {
        Comment comment = commentService.getCommentById(id);
        Post post = postService.getPostById(postId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        if (user.getAuthority().equals("author")) {
            if (!user.getPosts().contains(post)) {
                return "access-denied";
            }
        }

        model.addAttribute("name", comment.getName());
        model.addAttribute("email", comment.getEmail());
        model.addAttribute("comment", comment.getComment());
        model.addAttribute("id", comment.getId());
        model.addAttribute("postId", postId);
        return "update-comment";
    }

    @RequestMapping("/commentupdationcompletion")
    public String updatedComment(@RequestParam("id") int postId,
                                 @RequestParam("commentid") int commentId,
                                 @RequestParam("name") String name,
                                 @RequestParam("email") String email,
                                 @RequestParam("comment") String comment,
                                 RedirectAttributes redirectAttributes) {
        Comment commentObj = commentService.getCommentById(commentId);
        commentObj.setName(name);
        commentObj.setEmail(email);
        commentObj.setComment(comment);
        commentService.save(commentObj);
        redirectAttributes.addAttribute("id", postId);
        return "redirect:/viewpost";
    }
}
