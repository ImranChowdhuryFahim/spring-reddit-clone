package com.firstproject.springredditclone.service;

import com.firstproject.springredditclone.dto.SubredditDto;
import com.firstproject.springredditclone.model.Subreddit;
import com.firstproject.springredditclone.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {
    public final SubredditRepository subredditRepository;

    public SubredditDto save(SubredditDto subredditDto)
    {
      Subreddit subreddit = subredditRepository.save(mapSubredditDto(subredditDto));
      subredditDto.setId(subreddit.getId());
      return subredditDto;
    }

    private Subreddit mapSubredditDto(SubredditDto subredditDto) {
       return Subreddit.builder().name(subredditDto.getName())
                .description(subredditDto.getDescription())
                .build();
    }

    public List<SubredditDto> getAll()
    {
        return subredditRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(toList());

    }

    private SubredditDto mapToDto(Subreddit subreddit)
    {
        return SubredditDto.builder().name(subreddit.getName())
                .id(subreddit.getId())
                .description(subreddit.getDescription())
                .numberOfPosts(subreddit.getPosts().size())
                .build();

    }

}
