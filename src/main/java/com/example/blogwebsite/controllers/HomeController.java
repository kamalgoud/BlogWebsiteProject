package com.example.blogwebsite.controllers;

import com.example.blogwebsite.entities.Post;
import com.example.blogwebsite.entities.User;
import com.example.blogwebsite.repositories.PostRepository;
import com.example.blogwebsite.repositories.TagRepository;
import com.example.blogwebsite.repositories.UserRepository;
import com.example.blogwebsite.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    int i = 0;

    @Autowired
    public HomeController(PostService postService, PostRepository postRepository, TagRepository tagRepository, UserRepository userRepository) {
        this.postService = postService;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    @RequestMapping("/")
    public String home(@RequestParam(defaultValue = "0") Integer page,
                       @RequestParam(defaultValue = "createdAt") String sortField,
                       @RequestParam(defaultValue = "desc") String sortOrder,
                       @RequestParam(required = false) Set<String> authorNames,
                       @RequestParam(required = false) Set<Integer> tagIds,
                       @RequestParam(required = false) String searchTerm,
                       Model model) {
        int pageSize = 6;
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortField);
        PageRequest pageRequest = PageRequest.of(page != null ? page : 0, pageSize, sort);
        Page<Post> postPage;

        if (authorNames != null && authorNames.isEmpty()) {
            authorNames = null;
        }
        if (tagIds != null && tagIds.isEmpty()) {
            tagIds = null;
        }
        if (searchTerm == null) {
            searchTerm = "";
        }

        searchTerm = searchTerm.trim();

        if ((!searchTerm.isEmpty()) || (authorNames != null) || tagIds != null) {
            postPage = postRepository.filterPosts(authorNames, tagIds, searchTerm, pageRequest);
            System.out.println(postPage.getContent() + " 111 ");
        } else {
            postPage = postService.getPostsWithPaginationAndSorting(page != null ? page : 0, pageSize, sort);
        }

        String authorNamesStr = authorNames != null ? String.join(",", authorNames) : "";
        String tagIdsStr = tagIds != null ? tagIds.stream().map(Object::toString).collect(Collectors.joining(",")) : "";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());

        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        model.addAttribute("user", authentication);
        model.addAttribute("authorUrl", authorNamesStr);
        model.addAttribute("tagIdUrl", tagIdsStr);
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page != null ? page : 0);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("authorNames", authorNames);
        model.addAttribute("tagIds", tagIds);
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("authors", postRepository.findAllAuthors());

        return "home";
    }

}
