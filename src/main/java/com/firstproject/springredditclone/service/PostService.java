package com.firstproject.springredditclone.service;

import com.firstproject.springredditclone.dto.PostRequest;
import com.firstproject.springredditclone.dto.PostResponse;
import com.firstproject.springredditclone.exceptions.PostNotFoundException;
import com.firstproject.springredditclone.exceptions.SpringRedditException;
import com.firstproject.springredditclone.exceptions.SubredditNotFoundException;
import com.firstproject.springredditclone.mapper.PostMapper;
import com.firstproject.springredditclone.model.Post;
import com.firstproject.springredditclone.model.Subreddit;
import com.firstproject.springredditclone.model.User;
import com.firstproject.springredditclone.repository.PostRepository;
import com.firstproject.springredditclone.repository.SubredditRepository;
import com.firstproject.springredditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final SubredditRepository subredditRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PostMapper postMapper;

    public void save(PostRequest postRequest) {

            System.out.println(postRequest.toString());
        System.out.println(postRequest.getSubredditName());

            Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                    .orElseThrow(() -> new SpringRedditException("No Subreddit found as: "+(postRequest.getSubredditName())));
        System.out.println(subreddit.toString());
           Post save = postRepository.save(postMapper.map(postRequest, subreddit, authService.getCurrentUser()));

           // add post to subreddit
            List<Post> posts = subreddit.getPosts();
            if(posts == null) posts = new ArrayList<>();
            posts.add(save);
            subredditRepository.save(subreddit);

    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("No Post found as: "+id.toString()));
        return postMapper.mapToDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::mapToDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SubredditNotFoundException("No Subreddit found as: "+subredditId.toString()));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream().map(postMapper::mapToDto).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No User found as: "+username));
        return postRepository.findByUser(user)
                .stream()
                .map(postMapper::mapToDto)
                .collect(toList());
    }
}