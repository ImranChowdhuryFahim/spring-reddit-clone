package com.firstproject.springredditclone.repository;

import com.firstproject.springredditclone.model.Post;
import com.firstproject.springredditclone.model.Subreddit;
import com.firstproject.springredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findByUser(User user);
}
