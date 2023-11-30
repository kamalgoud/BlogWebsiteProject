package com.example.blogwebsite.services;

import com.example.blogwebsite.entities.Comment;
import com.example.blogwebsite.entities.Tag;
import com.example.blogwebsite.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TagService {
    TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public void save(Tag tag) {
        tagRepository.save(tag);
    }

    public Set<Tag> getTagByPostId(int postId) {
        return tagRepository.findTagsByPostId(postId);
    }

    public Tag getTagByName(String name) {
        return tagRepository.findByName(name);
    }
}
