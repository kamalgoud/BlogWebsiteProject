package com.example.blogwebsite.controllers;

import com.example.blogwebsite.entities.Comment;
import com.example.blogwebsite.entities.Post;
import com.example.blogwebsite.entities.Tag;
import com.example.blogwebsite.entities.User;
import com.example.blogwebsite.repositories.PostRepository;
import com.example.blogwebsite.repositories.UserRepository;
import com.example.blogwebsite.services.CommentService;
import com.example.blogwebsite.services.PostService;
import com.example.blogwebsite.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final TagService tagService;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    @Autowired
    public PostController(PostService postService, CommentService commentService,
                          TagService tagService, PostRepository postRepository, UserRepository userRepository) {
        this.postService = postService;
        this.commentService = commentService;
        this.tagService = tagService;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @RequestMapping("/newpost")
    public String createPost(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        Post post = new Post();

        post.setAuthor(user.getName());

        model.addAttribute("name", user.getName());
        model.addAttribute("role", user.getAuthority());
        model.addAttribute("post", post);
        return "create-post";
    }

    @RequestMapping("/postcreated")
    public String savePost(@RequestParam("tag") String tags,
                           @ModelAttribute Post post) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());

        if (user.getAuthority().equals("author")) {
            post.setAuthor(user.getName());
        }

        String[] s = tags.split(",");
        Set<Tag> tagsSet = new HashSet<>();

        for (int i = 0; i < s.length; i++) {
            Tag tag;

            if (tagService.getTagByName(s[i].trim()) != null) {
                tag = tagService.getTagByName(s[i].trim());
            } else {
                tag = new Tag();
            }

            tag.setName(s[i].trim());
            tagService.save(tag);
            tagsSet.add(tag);
        }
        post.setTags(tagsSet);
        postService.savePost(post);
        user.getPosts().add(post);
        userRepository.save(user);
        return "redirect:/";
    }

    @RequestMapping("/viewpost")
    public String viewPost(@RequestParam("id") int id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);

        List<Comment> comments = postService.getCommentsByPostId(id);
        model.addAttribute("comments", comments);

        return "view-post";
    }

    @RequestMapping("/deletepost")
    public String deletePost(@RequestParam("id") int id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());

        Post post = postService.getPostById(id);

        if (user.getAuthority().equals("author")) {
            if (!user.getPosts().contains(post)) {
                return "access-denied";
            }
            user.getPosts().remove(post);
            userRepository.save(user);
        }

        post.getComments().clear(); // Remove the comments from the post
        postRepository.delete(post); // Delete the post

        return "redirect:/";
    }

    @RequestMapping("/updatepost")
    public String updatePost(@RequestParam("id") int id, Model model) {
        Post post = postService.getPostById(id);
        Set<Tag> tags = tagService.getTagByPostId(id);
        ArrayList<String> tagsList = new ArrayList<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());

        if (user.getAuthority().equals("author")) {
            if (!user.getPosts().contains(post)) {
                return "access-denied";
            }
        }

        for (Tag tag : tags) {
            tagsList.add(tag.getName());
        }

        String commaSeperatedTags = String.join(",", tagsList);

        model.addAttribute("name", user.getName());
        model.addAttribute("role", user.getAuthority());
        model.addAttribute("tag", commaSeperatedTags);
        model.addAttribute(post);

        return "update-post";
    }

    @RequestMapping("/postupdationcompletion")
    public String updatedPost(@RequestParam("id") int id, @ModelAttribute Post post, @RequestParam("tag") String tags,
                              RedirectAttributes redirectAttributes) {
        String[] s = tags.split(",");
        Set<Tag> tagsSet = new HashSet<>();

        for (int i = 0; i < s.length; i++) {
            Tag tag;

            if (tagService.getTagByName(s[i].trim()) != null) {
                tag = tagService.getTagByName(s[i].trim());
            } else {
                tag = new Tag();
            }

            tag.setName(s[i].trim());
            tagService.save(tag);
            tagsSet.add(tag);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());

        if (user.getAuthority().equals("author")) {
            post.setAuthor(user.getName());
        }

        post.setTags(tagsSet);
        postService.updatePost(post);

        redirectAttributes.addAttribute("id", id);
        return "redirect:/viewpost";
    }

}
