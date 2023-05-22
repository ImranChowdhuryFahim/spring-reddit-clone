package com.firstproject.springredditclone.repository;

import com.firstproject.springredditclone.model.Post;
import com.firstproject.springredditclone.model.User;
import com.firstproject.springredditclone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
