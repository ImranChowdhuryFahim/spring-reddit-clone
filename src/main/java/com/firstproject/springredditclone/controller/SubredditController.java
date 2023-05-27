package com.firstproject.springredditclone.controller;

import com.firstproject.springredditclone.dto.PostResponse;
import com.firstproject.springredditclone.dto.SubredditDto;
import com.firstproject.springredditclone.model.Post;
import com.firstproject.springredditclone.service.SubredditService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subreddits")
@AllArgsConstructor
@Slf4j
@Tag(name="Subreddit")
@SecurityRequirement(name="jwt-auth")
public class SubredditController {
    public final SubredditService subredditService;

    @PostMapping
    public ResponseEntity<SubredditDto> createSubreddit(@RequestBody SubredditDto subredditDto)
    {
       return ResponseEntity.status(HttpStatus.CREATED)
                .body(subredditService.save(subredditDto));
    }

    @GetMapping
    public ResponseEntity<List<SubredditDto>> getAllSubreddits()
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(subredditService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubredditDto> getSubreddit(@PathVariable Long id)
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(subredditService.getSubreddit(id));
    }

    @GetMapping("{id}/posts")
    public ResponseEntity<List<PostResponse>> getAllSubredditPosts(@PathVariable Long id)
    {
        return ResponseEntity.status(HttpStatus.OK).body(subredditService.getSubredditPosts(id));
    }
}
